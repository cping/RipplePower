@ECHO OFF

for %%X in (java.exe) do (set IS_JAVA_IN_PATH=%%~$PATH:X)

IF defined IS_JAVA_IN_PATH (
	start "RipplePower" java -cp demo.jar;libs\*;conf org.ripple.power.ui.MainUI
) ELSE (
	IF EXIST "%PROGRAMFILES%\Java\jre7" (
		start "NXT NRS" "%PROGRAMFILES%\Java\jre7\bin\java.exe" -cp demo.jar;libs\*;conf org.ripple.power.ui.MainUI
	) ELSE IF EXIST "%PROGRAMFILES%\Java\jre8" (
		start "NXT NRS" "%PROGRAMFILES%\Java\jre8\bin\java.exe" -cp demo.jar;libs\*;conf org.ripple.power.ui.MainUI
	) ELSE (
		IF EXIST "%PROGRAMFILES(X86)%\Java\jre7" (
			start "NXT NRS" "%PROGRAMFILES(X86)%\Java\jre7\bin\java.exe" -cp nxt.jar;lib\*;conf nxt.Nxt
		) ELSE (
			ECHO Java software not found on your system. Please go to http://java.com/en/ to download a copy of Java.
			PAUSE
		)
	)
)
