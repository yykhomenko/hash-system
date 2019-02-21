@echo off
for /r %2 %%f in (*.sql) do (
    echo %0: %%~nf: loading...
    @echo off
    %1\bin\cqlsh.bat -C -f %%f
    echo %0: %%~nf: done
)
echo %0: completed
