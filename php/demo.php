<?php
	set_time_limit(0);
	date_default_timezone_set("Asia/Chongqing");
	header("Content-Type:text/html,charset=utf-8");
	include "swjtuXszc.class.php";
	
	$username = "";
	$password = "";
	$cc = new swjtuXszc($username,$password);
	$videoList = $cc -> grap_list();
	echo $cc -> login()."<br>";
	echo $cc -> chooseVideo($videoList)."<br>";
	echo $cc -> cheat("ADD")."<br>";
	ob_flush();
	flush();
	sleep(120);
	echo $cc -> cheat("STOP");
?>