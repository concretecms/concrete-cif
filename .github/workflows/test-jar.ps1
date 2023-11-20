Trap { 
    Write-Error $_ -ErrorAction Continue
    exit 1 
}
$Global:ErrorActionPreference = 'Stop'
$Global:ProgressPreference = 'SilentlyContinue'

Set-Location -LiteralPath "$Env:TEMP"

Write-Host -NoNewline -Object 'Copying jar to temporary directory... '
Copy-Item -LiteralPath "$Env:GITHUB_WORKSPACE\concrete-cif.jar" -Destination .
Write-Host -Object 'done.'

Write-Host -NoNewline -Object 'Test good CIFs with jar... '
& java -jar concrete-cif.jar "$Env:GITHUB_WORKSPACE\src\test\resources\cifs-good" >concrete-cif.jar.log 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host -Object 'FAILED!'
    Get-Content -LiteralPath 'concrete-cif.jar.log'
    exit 1
}
Write-Host -Object 'passed.'

Write-Host -NoNewline -Object 'Test bad CIFs with jar... '
$Global:ErrorActionPreference = 'SilentlyContinue'
& java -jar concrete-cif.jar "$Env:GITHUB_WORKSPACE\src\test\resources\cifs-bad" >concrete-cif.jar.log 2>&1
if ($LASTEXITCODE -ne 1) {
    Write-Host -Object 'FAILED!'
    Get-Content -LiteralPath 'concrete-cif.jar.log'
    exit 1
}
$Global:ErrorActionPreference = 'Stop'
$Global:LASTEXITCODE = 0
Write-Host -Object 'passed.'

Remove-Item -LiteralPath 'concrete-cif.jar'
Remove-Item -LiteralPath 'concrete-cif.jar.log'
