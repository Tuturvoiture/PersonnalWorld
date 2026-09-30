# Generate PATCHNOTES for build/libs/* and builds/* from docs/CHANGELOG_WIP.md
# alpha = delta ; beta/stable = aggregate + topic dedupe
# Usage: powershell -File script\generate-patchnotes.ps1 [-All]

param([switch]$All)

$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
if (-not (Test-Path (Join-Path $Root "gradle.properties"))) {
	$Root = (Get-Location).Path
}

function Parse-ModVer([string]$raw) {
	if ($raw -match '^(\d+)\.(\d+)\.(\d+)(?:-(alpha|beta)\.(\d+))?$') {
		$ch = if ($Matches[4] -eq 'alpha') { 0 } elseif ($Matches[4] -eq 'beta') { 1 } else { 2 }
		$pre = if ($Matches[5]) { [int]$Matches[5] } else { 0 }
		return [pscustomobject]@{
			Major = [int]$Matches[1]; Minor = [int]$Matches[2]; Patch = [int]$Matches[3]
			Channel = $ch; Pre = $pre; Raw = $raw
		}
	}
	return $null
}

function Compare-ModVer($a, $b) {
	foreach ($k in @('Major','Minor','Patch','Channel','Pre')) {
		$d = $a.$k - $b.$k
		if ($d -ne 0) { return $d }
	}
	return 0
}

function Topic-Key([string]$text) {
	$t = $text.ToLowerInvariant()
	if ($t.Contains('spawn_marker') -or ($t.Contains('spawn') -and $t.Contains('marker'))) { return 'spawn_marker' }
	if ($t.Contains('geckolib') -or $t.Contains('baton')) { return 'staff_geckolib' }
	if ($t.Contains('facade ui') -or $t.Contains('islandmembersapi') -or $t.Contains('s2c')) { return 'ui_sync' }
	if ($t.Contains('/pw') -or $t.Contains('invite') -or $t.Contains('whitelist')) { return 'invites_access' }
	if ($t.Contains('temp') -and ($t.Contains('visit') -or $t.Contains('guest') -or $t.Contains('kick'))) { return 'invites_access' }
	if ($t.Contains('darchitect') -or $t.Contains('dimensionarchitect')) { return 'darchitect' }
	if ($t.Contains('tomlj')) { return 'tomlj' }
	if ($t.Contains('inventaire') -or $t.Contains('shareinventory') -or $t.Contains('isolateplayerdata')) { return 'inventory' }
	if ($t.Contains('returnworld') -or $t.StartsWith('retour') -or ($t.Contains('retour') -and ($t.Contains('dim') -or $t.Contains('monde') -or $t.Contains('save')))) { return 'return' }
	if ($t.Contains('dimlib')) { return 'dimlib' }
	if ($t.Contains('hors-scope') -or $t.Contains('hors scope')) { return 'hors_scope_doc' }
	if ($t.Contains('personnalworld.toml') -or ($t.Contains('config') -and $t.Contains('toml'))) { return 'config_toml' }
	if ($t.Contains('init ile') -or ($t.Contains('synchrone') -and $t.Contains('tp'))) { return 'island_init' }
	$words = @($t -replace '[^\p{L}\p{N}\s]',' ' -split '\s+' | Where-Object { $_.Length -gt 2 } | Select-Object -First 5)
	if ($words.Count -eq 0) { return "misc_$($text.GetHashCode())" }
	return "misc_$($words -join '_')"
}

$wipPath = Join-Path $Root "docs\CHANGELOG_WIP.md"
$bullets = New-Object System.Collections.Generic.List[object]
Get-Content $wipPath -Encoding UTF8 | ForEach-Object {
	$line = $_.Trim()
	# - 1.2.3 - text   or em-dash variants: any non-space separator
	if ($line -match '^-\s+(\d+\.\d+\.\d+(?:-(?:alpha|beta)\.\d+)?)\s+\S+\s+(.+)$') {
		$v = Parse-ModVer $Matches[1]
		if ($v) { [void]$bullets.Add([pscustomobject]@{ Ver = $v; Text = $Matches[2].Trim() }) }
	}
}

$libsRoot = Join-Path $Root "build\libs"
$fromLibs = New-Object System.Collections.Generic.List[object]
if (Test-Path $libsRoot) {
	Get-ChildItem $libsRoot -Directory | ForEach-Object {
		$v = Parse-ModVer $_.Name
		if ($v) { [void]$fromLibs.Add($v) }
	}
}

$props = Get-Content (Join-Path $Root "gradle.properties") | Where-Object { $_ -match '^mod\.version=' }
$currentRaw = ($props -replace 'mod\.version=','').Trim()
if ($All) {
	$targets = @($fromLibs | ForEach-Object { $_.Raw })
	if ($targets -notcontains $currentRaw) { $targets += $currentRaw }
} else {
	$targets = @($currentRaw)
}

$git = "unknown"
try {
	$g = & git -C $Root rev-parse --short HEAD 2>$null
	if ($g) { $git = "$g" }
} catch {}
$builtAt = [DateTime]::UtcNow.ToString("o")

$knownList = New-Object System.Collections.Generic.List[object]
foreach ($v in $fromLibs) { [void]$knownList.Add($v) }
foreach ($b in $bullets) { [void]$knownList.Add($b.Ver) }

function Select-Bullets($current, $allBullets, $known) {
	$prev = $null
	$lines = @()
	if ($current.Channel -eq 0) {
		$cands = @($known | Where-Object { (Compare-ModVer $_ $current) -lt 0 })
		if ($cands.Count -gt 0) {
			$prev = $cands | Sort-Object Major, Minor, Patch, Channel, Pre | Select-Object -Last 1
		}
		$lines = @($allBullets | Where-Object { $_.Ver.Raw -eq $current.Raw } | ForEach-Object { $_.Text })
	} else {
		$cands = @($known | Where-Object { $_.Channel -eq $current.Channel -and (Compare-ModVer $_ $current) -lt 0 })
		if ($cands.Count -gt 0) {
			$prev = $cands | Sort-Object Major, Minor, Patch, Channel, Pre | Select-Object -Last 1
		}
		$inRange = @($allBullets | Where-Object {
			$cmpCur = Compare-ModVer $_.Ver $current
			$afterPrev = if ($null -eq $prev) { $true } else { (Compare-ModVer $_.Ver $prev) -gt 0 }
			($cmpCur -le 0) -and $afterPrev
		} | Sort-Object { $_.Ver.Major }, { $_.Ver.Minor }, { $_.Ver.Patch }, { $_.Ver.Channel }, { $_.Ver.Pre })
		$map = @{}
		foreach ($b in $inRange) { $map[(Topic-Key $b.Text)] = $b }
		$lines = @($map.Values | Sort-Object { $_.Ver.Major }, { $_.Ver.Minor }, { $_.Ver.Patch }, { $_.Ver.Channel }, { $_.Ver.Pre } -Descending | ForEach-Object { $_.Text })
	}
	return @{ Prev = $prev; Lines = $lines }
}

$labels = @{
	0 = "alpha (delta de cette version)"
	1 = "beta (agregat depuis la derniere beta, sans doublons thematiques)"
	2 = "official/stable (agregat depuis la derniere stable, sans doublons thematiques)"
}

$utf8 = New-Object System.Text.UTF8Encoding $false

foreach ($verRaw in $targets) {
	$current = Parse-ModVer $verRaw
	if (-not $current) { Write-Warning "Skip invalid version $verRaw"; continue }
	$knownWith = @($knownList.ToArray()) + @($current)
	$sel = Select-Bullets $current $bullets $knownWith
	$chName = switch ($current.Channel) { 0 { 'alpha' } 1 { 'beta' } default { 'stable' } }
	if ($sel.Prev) {
		if ($current.Channel -eq 0) {
			$sinceLine = "- **Depuis :** ``$($sel.Prev.Raw)`` (version precedente)"
		} else {
			$sinceLine = "- **Depuis :** ``$($sel.Prev.Raw)`` (dernier canal $chName)"
		}
	} else {
		$sinceLine = "- **Depuis :** _(aucune version anterieure du meme canal)_"
	}
	if ($sel.Lines.Count -eq 0) {
		$body = "_Aucun bullet WIP trouve pour cette plage - completer docs/CHANGELOG_WIP.md._"
	} else {
		$body = ($sel.Lines | ForEach-Object { "- $_" }) -join "`n"
	}

	$sb = New-Object System.Text.StringBuilder
	[void]$sb.AppendLine("# Patch notes - $($current.Raw)")
	[void]$sb.AppendLine()
	[void]$sb.AppendLine("- **Canal :** $($labels[$current.Channel])")
	[void]$sb.AppendLine($sinceLine)
	[void]$sb.AppendLine("- **Build (UTC) :** $builtAt")
	[void]$sb.AppendLine("- **Git :** ``$git``")
	[void]$sb.AppendLine("- **Capacites :** voir ``docs/CAPABILITIES.md``")
	[void]$sb.AppendLine("- **Source :** ``docs/CHANGELOG_WIP.md`` (Pending)")
	[void]$sb.AppendLine()
	[void]$sb.AppendLine("---")
	[void]$sb.AppendLine()
	[void]$sb.AppendLine("## Changements")
	[void]$sb.AppendLine()
	[void]$sb.AppendLine($body)
	[void]$sb.AppendLine()
	$notes = $sb.ToString()

	$libsDir = Join-Path $libsRoot $verRaw
	New-Item -ItemType Directory -Force -Path $libsDir | Out-Null
	[System.IO.File]::WriteAllText((Join-Path $libsDir "PATCHNOTES.md"), $notes, $utf8)

	$loaders = New-Object System.Collections.Generic.List[string]
	if (Get-ChildItem (Join-Path $libsDir "fabric") -Filter "*.jar" -ErrorAction SilentlyContinue) { [void]$loaders.Add('fabric') }
	if (Get-ChildItem (Join-Path $libsDir "neoforge") -Filter "*.jar" -ErrorAction SilentlyContinue) { [void]$loaders.Add('neoforge') }
	$loaderStr = if ($loaders.Count) { $loaders -join ', ' } else { '_(none yet)_' }
	$metaSb = New-Object System.Text.StringBuilder
	[void]$metaSb.AppendLine("# META - $verRaw")
	[void]$metaSb.AppendLine()
	[void]$metaSb.AppendLine("- **Built (UTC) :** $builtAt")
	[void]$metaSb.AppendLine("- **Git :** ``$git``")
	[void]$metaSb.AppendLine("- **Loaders :** $loaderStr")
	[void]$metaSb.AppendLine()
	$meta = $metaSb.ToString()
	[System.IO.File]::WriteAllText((Join-Path $libsDir "META.md"), $meta, $utf8)

	$buildsDir = Join-Path $Root "builds\$verRaw"
	New-Item -ItemType Directory -Force -Path $buildsDir | Out-Null
	[System.IO.File]::WriteAllText((Join-Path $buildsDir "PATCHNOTES.md"), $notes, $utf8)
	[System.IO.File]::WriteAllText((Join-Path $buildsDir "META.md"), $meta, $utf8)
	Write-Host "PATCHNOTES written for $verRaw"
}

$buildsRoot = Join-Path $Root "builds"
$vers = @()
Get-ChildItem $buildsRoot -Directory -ErrorAction SilentlyContinue | ForEach-Object {
	$v = Parse-ModVer $_.Name
	if ($v) { $vers += $v }
}
$vers = @($vers | Sort-Object Major, Minor, Patch, Channel, Pre -Descending)
$idx = New-Object System.Text.StringBuilder
[void]$idx.AppendLine("# Archive des builds")
[void]$idx.AppendLine()
[void]$idx.AppendLine("Historique durable par ``mod.version`` - hors ``build/`` Gradle (ephemere).")
[void]$idx.AppendLine()
[void]$idx.AppendLine("| Version | Dernier build (UTC) | Notes |")
[void]$idx.AppendLine("|---------|---------------------|-------|")
foreach ($v in $vers) {
	[void]$idx.AppendLine("| $($v.Raw) | $builtAt | [PATCHNOTES]($($v.Raw)/PATCHNOTES.md) / [META]($($v.Raw)/META.md) |")
}
[void]$idx.AppendLine()
[void]$idx.AppendLine("Voir ``docs/VERSIONING.md`` section Archive builds/.")
[void]$idx.AppendLine()
[System.IO.File]::WriteAllText((Join-Path $buildsRoot "INDEX.md"), $idx.ToString(), $utf8)
Write-Host "INDEX updated."
