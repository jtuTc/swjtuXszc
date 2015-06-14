#encoding:utf-8
import time
import swjtu_xszc
username = ""
password = ""
cc = swjtu_xszc.swjtu_xszc(username,password)
video_list = cc.grap_list()
print cc.choose_Video(video_list)
print cc.login()
print cc.cheat("ADD")
time.sleep(120)
print cc.cheat("STOP")