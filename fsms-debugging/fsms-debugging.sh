cat README
echo 'Please press any key to continue, or Ctrl+C to exit...'
read -n 1 -s
echo 'FrontlineSMS RXTX debugging' >> report.log
echo 'uname' >> report.log
uname -m >> report.log
java -jar rxtx-osx-debugging-0.1-jar-with-dependencies.jar info >> report.log

cp ./rxtx-libs/i686/librxtxSerial.jnilib .
echo '-----------' >> report.log
echo 'Using RXTX i686' >> report.log
md5 ./librxtxSerial.jnilib >> report.log
java -jar rxtx-osx-debugging-0.1-jar-with-dependencies.jar test >> report.log

cp ./rxtx-libs/x86_64/librxtxSerial.jnilib .
echo '-----------' >> report.log
echo 'Using RXTX x86_64' >> report.log
md5 ./librxtxSerial.jnilib >> report.log
java -jar rxtx-osx-debugging-0.1-jar-with-dependencies.jar test >> report.log
java -jar rxtx-osx-debugging-0.1-jar-with-dependencies.jar mail

echo '\o/ Thanks for helping us make FrontlineSMS as widely available as possible \o/'