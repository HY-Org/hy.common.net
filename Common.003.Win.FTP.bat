

cd .\bin

rd /s/q .\org\hy\common\net\junit
rd /s/q .\org\hy\common\net\plugins

jar cvfm hy.common.net.jar MANIFEST.MF LICENSE org

copy hy.common.net.jar ..
del /q hy.common.net.jar
cd ..

