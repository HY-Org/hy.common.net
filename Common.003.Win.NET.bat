

cd .\bin


rd /s/q .\org\hy\common\net\junit
rd /s/q .\org\hy\common\net\plugins

jar cvfm hy.common.net.jar MANIFEST.MF META-INF org

copy hy.common.net.jar ..
del /q hy.common.net.jar
cd ..





cd .\src
jar cvfm hy.common.net-sources.jar MANIFEST.MF META-INF org 
copy hy.common.net-sources.jar ..
del /q hy.common.net-sources.jar
cd ..
