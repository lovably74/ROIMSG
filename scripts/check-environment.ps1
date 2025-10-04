param(
    [switch]$Verbose
)

$checks = @(
    [pscustomobject]@{ Name = "Node.js"; Command = "node"; Arguments = @("--version"); MinVersion = "18.0.0"; IsRequired = $true },
    [pscustomobject]@{ Name = "npm"; Command = "npm"; Arguments = @("--version"); MinVersion = "9.0.0"; IsRequired = $true },
    [pscustomobject]@{ Name = "Java"; Command = "java"; Arguments = @("-version"); MinVersion = "21.0.0"; IsRequired = $true },
    [pscustomobject]@{ Name = "Maven"; Command = "mvn"; Arguments = @("-v"); MinVersion = "3.9.0"; IsRequired = $true },
    [pscustomobject]@{ Name = "PostgreSQL (psql)"; Command = "psql"; Arguments = @("--version"); MinVersion = "15.0.0"; IsRequired = $true },
    [pscustomobject]@{ Name = "Redis"; Command = "redis-cli"; Arguments = @("--version"); MinVersion = "7.0.0"; IsRequired = $true },
    [pscustomobject]@{ Name = "Docker"; Command = "docker"; Arguments = @("--version"); MinVersion = "20.10.0"; IsRequired = $false },
    [pscustomobject]@{ Name = "Docker Compose"; Command = "docker"; Arguments = @("compose", "version"); MinVersion = "2.20.0"; IsRequired = $false }
)

function Get-VersionNumber {
    param(
        [string]$Text
    )

    if ([string]::IsNullOrWhiteSpace($Text)) {
        return $null
    }

    $match = [regex]::Match($Text, '(\d+\.\d+(?:\.\d+)?)')
    if (-not $match.Success) {
        return $null
    }

    $value = $match.Value
    if (($value.Split('.')).Count -eq 2) {
        $value = "$value.0"
    }

    return [version]$value
}

function Invoke-Check {
    param(
        [pscustomobject]$Check
    )

    $required = $Check.IsRequired -ne $false
    $result = [ordered]@{
        Name = $Check.Name
        Status = "PASS"
        Detected = $null
        Minimum = $Check.MinVersion
        Notes = $null
        IsRequired = $required
    }

    try {
        $rawOutput = & $Check.Command @($Check.Arguments) 2>&1
        $rawText = ($rawOutput | Out-String).Trim()
        $version = Get-VersionNumber -Text $rawText

        if (-not $version) {
            $result.Status = "WARN"
            $result.Notes = "Version could not be detected. Output: $rawText"
            return [pscustomobject]$result
        }

        $result.Detected = $version.ToString()

        if ($Check.MinVersion) {
            $minVersion = [version]$Check.MinVersion
            if ($version -lt $minVersion) {
                if ($required) {
                    $result.Status = "FAIL"
                    $result.Notes = "Version $version is lower than required $minVersion"
                }
                else {
                    $result.Status = "WARN"
                    $result.Notes = "Optional dependency below recommended version ($version < $minVersion)"
                }
            }
        }
    }
    catch {
        if ($required) {
            $result.Status = "FAIL"
            $result.Notes = $_.Exception.Message
        }
        else {
            $result.Status = "WARN"
            $result.Notes = "Optional dependency missing (test/prod only): $($_.Exception.Message)"
        }
    }

    return [pscustomobject]$result
}

$results = foreach ($check in $checks) {
    Invoke-Check -Check $check
}

$javaHome = $env:JAVA_HOME
$additionalNotes = if ($javaHome) {
    "JAVA_HOME set to $javaHome"
} else {
    "JAVA_HOME is not set"
}

$results | Select-Object Name, Status, Detected, Minimum, Notes | Format-Table -AutoSize
Write-Host "`n$additionalNotes"

if ($Verbose) {
    Write-Host "`nDetailed output:" -ForegroundColor Cyan
    foreach ($check in $checks) {
        try {
            $raw = & $check.Command @($check.Arguments) 2>&1
            Write-Host "`n[$($check.Name)]" -ForegroundColor Yellow
            Write-Host (($raw | Out-String).Trim())
        }
        catch {
            Write-Host "`n[$($check.Name)]" -ForegroundColor Yellow
            Write-Host $_.Exception.Message
        }
    }
}

if ($results | Where-Object { $_.IsRequired -and $_.Status -eq "FAIL" }) {
    exit 1
}
