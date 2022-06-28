#!/bin/bash
project=KMReport
# 定义变量
bin=`dirname "$0"`
bin=`cd "$bin"; pwd`
# 要运行的jar包路径，加不加引号都行。 注意：等号两边 不能 有空格，否则会提示command找不到
JAR_NAME="${bin}/../jar/kmreport-*.jar"
# 将lib引入依赖
SETTING="-Dloader.path=${bin}/../lib/"

PIDFILE="KMReport.pid"
#检查PID path 是否存在
PIDPATH="${bin}/../run"
if [ -d ${PIDPATH} ];then
    echo "${PIDPATH} is already exist" >> /dev/null
else
    mkdir -p  ${PIDPATH}
fi

#检查PID File 是否存在
if [ -f "${PIDPATH}/${PIDFILE}" ];then
    echo "${PIDPATH}/${PIDFILE} is already exist" >> /dev/null
else
    touch ${PIDPATH}/${PIDFILE}
fi

# 如果输入格式不对，给出提示！
tips() {
  echo ""
  echo "WARNING!!!......Tips, please use command: sh auto.sh [start|stop|restart|status].   For example: sh auto.sh start  "
  echo ""
  exit 1
}

# 启动方法
start() {
  pid=$(cat ${PIDPATH}/${PIDFILE})
  if [ -z $pid ]; then
    nohup java -jar -Xms512M -Xmx2048M $JAR_NAME > log/kmreport-info.log 2>&1 &
    echo $! >${PIDPATH}/${PIDFILE}
    echo "........................................Start ${project} Successfully........................................"

  else
    echo "${project} pid $pid is in ${PIDPATH}/${PIDFILE}, Please stop first !!!"
  fi
}

# 停止方法
stop() {
  pid=$(cat ${PIDPATH}/${PIDFILE})
  if [ -z $pid ]; then
    echo "${project} pid is not exist in ${PIDPATH}/${PIDFILE}"
  else
    kill -9 $pid
    sleep 1
    echo "........................................Stop ${project} Successfully....................................."
    echo " " >${PIDPATH}/${PIDFILE}
  fi
}

# 输出运行状态方法
status() {
  # 重新获取一下pid，因为其它操作如stop、restart、start等会导致pid的状态更新
  pid=$(cat ${PIDPATH}/${PIDFILE})
  if [ -z $pid ]; then
    echo ""
    echo "Service ${JAR_NAME} is not running!"
    echo ""
  else
    echo ""
    echo "Service ${JAR_NAME} is running. It's pid=${pid}"
    echo ""
  fi
}

# 重启方法
restart() {
  echo ""
  stop
  start
  echo "........................................Restart Successfully........................................"
}

# 根据输入参数执行对应方法，不输入则执行tips提示方法
case "$1" in
"start")
  start
  ;;
"stop")
  stop
  ;;
"status")
  status
  ;;
"restart")
  restart
  ;;
*)
  tips
  ;;
esac
