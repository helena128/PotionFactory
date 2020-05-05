#!/usr/bin/env bash

SSHUSER="${SSHUSER:-s207220}@"
test "$SSHUSER" == "@" && SSHUSER=""
SSHPORT="${SSHPORT:-2222}"
SECRET="${SECRET:-my-super-secret}"
PORT=${PORT:-55000}
FWDPORT="${FWDPORT:-$PORT}"
BUILDPATH="${BUILD:-./target/universal/potion-factory-1.0-SNAPSHOT.zip}"

BUILDAR="$(basename "${BUILDPATH}")"
BUILDNAME="$(basename "${BUILDPATH}" .zip)"

echo "[INFO] Load archive to the server"
scp -P ${SSHPORT} "${BUILDPATH}" ${SSHUSER}se.ifmo.ru:

echo "[INFO] Setting up ssh tunnel for ${SSHUSER}se.ifmo.ru:${SSHPORT} at localhost:${FWDPORT}"
ssh -fNL ${FWDPORT}:localhost:${PORT} "${SSHUSER}se.ifmo.ru"
fwdpid=$$

echo "[INFO] Remotely preparing release on the server"
ssh "${SSHUSER}se.ifmo.ru" -p ${SSHPORT} bash -c "
echo \"[INFO] Ensuring complete cleanup after previous run (kill process, remove RUNNING_PID file\"
rp=./potion-factory/${BUILDNAME}/RUNNING_PID
if [ -e \$rp ]; then
  kill \$(cat \$rp)
  sleep 5
  kill -9 \$(cat \$rp)
  rm \$rp
fi


if [ ! -x ~/.local/bin/awk ]; then
  echo \"[INFO] Launch script depends on newer version of awk: shadow prehistoric awk with nawk\"
  mkdir -p ~/.local/bin
  ln -s \$(which nawk) ~/.local/bin/awk
fi
export PATH=\"~/.local/bin:\$PATH\"

echo \"[INFO] Setting ENV variables to set JDK to version 8\"
JAVA_PATH=\"/usr/jdk/jdk1.8.0\"
JAVA_HOME=\"\$JAVA_PATH\"
PATH=\"\$JAVA_PATH/bin/:\$PATH\"

echo \"[INFO] Ensuring directory structure for release\"
cd
mkdir -p potion-factory

echo \"[INFO] Unpacking release\"
echo A | unzip \"${BUILDAR}\" -d potion-factory

echo \"[INFO] Removing release archive\"
rm \"${BUILDAR}\"
"
#
#echo \"[INFO] Starting release\"
#./potion-factory/${BUILDNAME}/bin/potion-factory \\
# -Dplay.http.secret.key=${SECRET} \\
# -Dhttp.port=${PORT}

kill $fwdpid