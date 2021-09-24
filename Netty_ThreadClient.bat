@ECHO ON

set classpath=./bin;./lib/*;./lib/netty/*;./lib/protobuf/*;%classpath%

java -d64 -Xms1024m -Xmx6144m -Xss1024K -XX:PermSize=128m org.hy.common.net.junit.netty.thread.ThreadClient


PAUSE