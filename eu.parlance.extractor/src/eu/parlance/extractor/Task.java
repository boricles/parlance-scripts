package eu.parlance.extractor;

public class Task {

	public Task() {
		
	}
	
	String id;
	String taskID;
	String token;
	String q1,q2,q3,q4,q5,q6,q7,q8;
	String pwd;
	String timestamp;
	String logfile;
	
	String goal;
	
	String userTurns;
	String systemTurns;
	
	String length;
	
	double avgWordsPerSystemTurn;
	
	String helloSysDact;
	String confirmSysDact;
	String requestSysDact;
	String informSysDact;
	String nullSysDact;
	String givetokenSysDact;
	String goodbyeSysDact;
	String helloUserSemihyp;
	String confirmUserSemihyp;
	String informUserSemihyp;
	String givetokenUserSemihyp;
	String requestUserSemihyp;
	String nullUserSemihyp;
	String byeUserSemihyp;
	String afirmUserSemihyp;
	String repeatUserSemihyp;
	String helpUserSemihyp;
	String restartUserSemihyp;
	
	String audio;
	String system;
	
	public String getSystem() {
		return system;
	}

	public void setSystem(String system) {
		this.system = system;
	}

	public String getAudio() {
		return audio;
	}

	public void setAudio(String audio) {
		this.audio = audio;
	}

	public String getByeUserSemihyp() {
		return byeUserSemihyp;
	}

	public void setByeUserSemihyp(String byeUserSemihyp) {
		this.byeUserSemihyp = byeUserSemihyp;
	}

	public void setGoal(String goal) {	
		this.goal = goal;
	}
	
	public String getGoal() {
		return goal;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTaskID() {
		return taskID;
	}
	public void setTaskID(String taskID) {
		this.taskID = taskID;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getQ1() {
		return q1;
	}
	public void setQ1(String q1) {
		this.q1 = q1;
	}
	public String getQ2() {
		return q2;
	}
	public void setQ2(String q2) {
		this.q2 = q2;
	}
	public String getQ3() {
		return q3;
	}
	public void setQ3(String q3) {
		this.q3 = q3;
	}
	public String getQ4() {
		return q4;
	}
	public void setQ4(String q4) {
		this.q4 = q4;
	}
	public String getQ5() {
		return q5;
	}
	public void setQ5(String q5) {
		this.q5 = q5;
	}
	public String getQ6() {
		return q6;
	}
	public void setQ6(String q6) {
		this.q6 = q6;
	}
	public String getQ7() {
		return q7;
	}
	public void setQ7(String q7) {
		this.q7 = q7;
	}
	public String getQ8() {
		return q8;
	}
	public void setQ8(String q8) {
		this.q8 = q8;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getLogfile() {
		return logfile;
	}
	public void setLogfile(String logfile) {
		this.logfile = logfile;
	}

	public String getUserTurns() {
		return userTurns;
	}

	public void setUserTurns(String userTurns) {
		this.userTurns = userTurns;
	}

	public String getSystemTurns() {
		return systemTurns;
	}

	public void setSystemTurns(String systemTurns) {
		this.systemTurns = systemTurns;
	}

	public String getLength() {
		return length;
	}

	public void setLength(String length) {
		this.length = length;
	}

	public double getAvgWordsPerSystemTurn() {
		return avgWordsPerSystemTurn;
	}

	public void setAvgWordsPerSystemTurn(double avgWordsPerSystemTurn) {
		this.avgWordsPerSystemTurn = avgWordsPerSystemTurn;
	}

	public String getHelloSysDact() {
		return helloSysDact;
	}

	public void setHelloSysDact(String helloSysDact) {
		this.helloSysDact = helloSysDact;
	}

	public String getRequestSysDact() {
		return requestSysDact;
	}

	public void setRequestSysDact(String requestSysDact) {
		this.requestSysDact = requestSysDact;
	}

	public String getInformSysDact() {
		return informSysDact;
	}

	public void setInformSysDact(String informSysDact) {
		this.informSysDact = informSysDact;
	}

	public String getNullSysDact() {
		return nullSysDact;
	}

	public void setNullSysDact(String nullSysDact) {
		this.nullSysDact = nullSysDact;
	}

	public String getGivetokenSysDact() {
		return givetokenSysDact;
	}

	public void setGivetokenSysDact(String givetokenSysDact) {
		this.givetokenSysDact = givetokenSysDact;
	}

	public String getGoodbyeSysDact() {
		return goodbyeSysDact;
	}

	public void setGoodbyeSysDact(String goodbyeSysDact) {
		this.goodbyeSysDact = goodbyeSysDact;
	}

	public String getInformUserSemihyp() {
		return informUserSemihyp;
	}

	public void setInformUserSemihyp(String informUserSemihyp) {
		this.informUserSemihyp = informUserSemihyp;
	}

	public String getRequestUserSemihyp() {
		return requestUserSemihyp;
	}

	public void setRequestUserSemihyp(String requestUserSemihyp) {
		this.requestUserSemihyp = requestUserSemihyp;
	}

	public String getConfirmSysDact() {
		return confirmSysDact;
	}

	public void setConfirmSysDact(String confirmSysDact) {
		this.confirmSysDact = confirmSysDact;
	}

	public String getHelloUserSemihyp() {
		return helloUserSemihyp;
	}

	public void setHelloUserSemihyp(String helloUserSemihyp) {
		this.helloUserSemihyp = helloUserSemihyp;
	}

	public String getConfirmUserSemihyp() {
		return confirmUserSemihyp;
	}

	public void setConfirmUserSemihyp(String confirmUserSemihyp) {
		this.confirmUserSemihyp = confirmUserSemihyp;
	}

	public String getGivetokenUserSemihyp() {
		return givetokenUserSemihyp;
	}

	public void setGivetokenUserSemihyp(String givetokenUserSemihyp) {
		this.givetokenUserSemihyp = givetokenUserSemihyp;
	}

	public String getNullUserSemihyp() {
		return nullUserSemihyp;
	}

	public void setNullUserSemihyp(String nullUserSemihyp) {
		this.nullUserSemihyp = nullUserSemihyp;
	}

	public String getAfirmUserSemihyp() {
		return afirmUserSemihyp;
	}

	public void setAfirmUserSemihyp(String afirmUserSemihyp) {
		this.afirmUserSemihyp = afirmUserSemihyp;
	}

	public String getRepeatUserSemihyp() {
		return repeatUserSemihyp;
	}

	public void setRepeatUserSemihyp(String repeatUserSemihyp) {
		this.repeatUserSemihyp = repeatUserSemihyp;
	}

	public String getHelpUserSemihyp() {
		return helpUserSemihyp;
	}

	public void setHelpUserSemihyp(String helpUserSemihyp) {
		this.helpUserSemihyp = helpUserSemihyp;
	}

	public String getRestartUserSemihyp() {
		return restartUserSemihyp;
	}

	public void setRestartUserSemihyp(String restartUserSemihyp) {
		this.restartUserSemihyp = restartUserSemihyp;
	}
}
