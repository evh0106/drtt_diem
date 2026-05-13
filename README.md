# DRTT_DIEM

## Project Overview
- The name of this repository has no meaning.
- The purpose of this project was to analyze a company's computer system.
- This is analysis data on the integrated production system of a steel manufacturer.

## specification
| Item | Version |
|---|---|
| Servlet | 2.4 |
| JSP | 1.2 |
| Java | 1.4 |
| Tomcat | 5.5 |

## Troubleshooting
- Encrypted Korean Characters - Converting EUC-KR Documents to UTF-8
```powershell
Get-ChildItem -Filter *.java -Recurse | ForEach-Object { $content = Get-Content $_.FullName -Encoding String; [System.IO.File]::WriteAllLines($_.FullName, $content, (New-Object System.Text.UTF8Encoding($false))) }
```

## ETC

### Shortcuts
- Bookmark Function
  + Install Bookmarks Extension
```
Ctrl + Alt + K - Toggle current line bookmark
Ctrl + Alt + J - Go to previous bookmark
Ctrl + Alt + L - Go to next bookmark
```