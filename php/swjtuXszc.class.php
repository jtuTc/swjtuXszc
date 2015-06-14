<?php
/*
 *  #sjwtu刷形势政策脚本 PHP版
	#Author	jtu唐
	#QQ		461881920
	#邮箱	jtutang@163.com
 */
class swjtuXszc{
	private $username;
	private $password;
	private $cookie;
	private $sourceID;
	private $loginUrl = "http://ocw.swjtu.edu.cn/servlet/UserLoginDataAction";
	private $checkUrl = "http://ocw.swjtu.edu.cn/websys/studentstudytime.jsp";
	private $listUrl = "http://ocw.swjtu.edu.cn/page/viewVideo.jsp?c_id=196";
	public function __construct($username,$password){
		$this -> username = $username;
		$this -> password = $password;
	}
	
	public function login(){
		$data = "UserName=".$this -> username."&Password=".$this -> password."&UserType=stu";
		$this -> cookie = tempnam("./temp", "COOKIE");
		$re = $this -> POST($this -> loginUrl, $data, $this -> cookie, 1);
		if(strstr($re,"登录成功"))return 1;
		else return 0;
	}
	
	public function chooseVideo($videoList){
		$rand = $videoList[rand(0,count($videoList[1]))];
		$this -> sourceID = explode("=", $rand)[1];
		return $this -> sourceID;
	}
	
	public function checkLogin(){
		$re = $this -> GET($this -> checkUrl, $this -> cookie);
		if(strstr($re, "登录或没有"))return FALSE;
		return TRUE;
	}
	public function cheat($type){
		if("ADD" == $type)$url = sprintf("http://ocw.swjtu.edu.cn/servlet/UserStudyRecordAction?resource_id=%s&SetType=%s&ranstring=&sid=&tt=%s",$this -> sourceID,$type,time());
		else $url = sprintf("http://ocw.swjtu.edu.cn/servlet/UserStudyRecordAction?resource_id=%s&SetType=%s&ranstring=&sid=%s&tt=%s",$this -> sourceID,$type,$this -> sid,time());
		$referer = sprintf("Referer:http://ocw.swjtu.edu.cn/websys/videoview.jsp?resource_id=%s",$this -> sourceID);
		$httpheader = array(
			$referer,
			'User-Agent:Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.111 Safari/537.36'
		);
		$re = $this -> GET($url, $this -> cookie,$httpheader);
		$re = simplexml_load_string($re);
		if("ADD" == $type)$this -> sid = $re->select_message;
		return $re->select_message;
	}
	
	public function grap_list(){
		$re = $this -> GET($this -> listUrl, $this -> cookie);
		preg_match_all('/<a href="(.*?)" target=_blank>/', $re, $arr);
		return $arr[1];
	}
	
	public function GET($url,$cookie,$header = array()){
		$ch = curl_init();
		curl_setopt($ch, CURLOPT_URL, $url);
		curl_setopt($ch, CURLOPT_COOKIEFILE, $cookie);
		curl_setopt($ch, CURLOPT_HTTPHEADER, $header);
		curl_setopt($ch, CURLOPT_HEADER, 0);
		curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
		$re = curl_exec($ch);
		curl_close($ch);
		return $re;
	}
	
	public function POST($url,$data,$cookie,$flag,$header = array()){
		$ch = curl_init();
		curl_setopt($ch, CURLOPT_URL, $url);
		curl_setopt($ch, CURLOPT_POST, 1);
		curl_setopt($ch, CURLOPT_POSTFIELDS, $data);
		if($flag)curl_setopt($ch, CURLOPT_COOKIEJAR, $cookie);
		else curl_setopt($ch, CURLOPT_COOKIEFILE, $cookie);
		curl_setopt($ch, CURLOPT_HEADER, 0);
		curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
		$re = curl_exec($ch);
		curl_close($ch);
		return $re;
	}
}
?>