#!/bin/sh
# RecipeTracker 演示菜单
# 提供两种模式：观看预录制演示 或 自己体验应用

while true; do
    clear
    echo ""
    echo "欢迎进入 RecipeTracker 演示环境"
    echo "默认等待5秒自动进入演示模式 您可以随时Ctrl + C打断"
    echo ""
    echo "1.  观看演示"
    echo "2.  快速体验"
    echo "3.  退出演示"
    echo ""
    printf "请选择 [1-3]: "
    if read -t 5 choice; then
        :
    else
        echo ""
        choice=1
        sleep 1
    fi
    
    case $choice in
        1)
            clear
            echo ""
            sleep 1
            asciinema play /app/demo.cast
            echo ""
            echo ""
            echo "按回车键返回菜单..."
            read _
            ;;
        2)
            clear
            echo "启动 RecipeTracker 请稍后..."
            echo ""
            java -Djline.terminal=dumb -jar /app/app.jar
            echo ""
            echo "应用已退出，按回车键返回菜单..."
            read _
            ;;
        3)
            clear
            echo "感谢体验 RecipeTracker 再见！"
            exit 0
            ;;
        *)
            echo "无效选择，请输入 1-3..."
            sleep 1
            ;;
    esac
done
