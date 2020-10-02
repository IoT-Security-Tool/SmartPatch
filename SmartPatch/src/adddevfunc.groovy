import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook

def isGoodBracket(String s){
    Stack<Character> a = new Stack<Character>()
    for(int i=0; i<s.length(); i++)
    {
        char c = s.charAt(i);
        if(c=='(') a.push(')');

        if(c==')')
        {
            if(a.empty()) return false
            if(a.pop() != c) return false
        }
    }
    if(!a.empty()) return false
    return true
}

def isMethod(method) {
    for (int i = 0; i < configlist.size(); i++) {
        def json = slurper.parseText(configlist[i])
        if (json.type == "MethodNode") {
            if(json.name == method){
                return true
            }
        }
    }
    return false
}
def whichMethod(int line){
    for (int i = 0; i < configlist.size(); i++) {
        def json = slurper.parseText(configlist[i])
        if (json.type == "MethodNode") {
            if(line>json.linenumber&&line<json.lastlinenumber)
                return json.name
        }
    }
    return ""
}



def getconfig(configlist,slurper,methodMap,sendEventlist,createEventlist,createEventnumberlist,sendEventnumberlist) {
    for (int i = 0; i < configlist.size(); i++) {
        def json = slurper.parseText(configlist[i])
        if (json.type == "MethodNode") {
            methodMap.put(json.name,json.linenumber)
        }else if(json.type=="sendEvent"){
            sendEventlist.add(configlist[i])
        }else if(json.type=="createEvent"){
            // creatEventindex=i
            createEventlist.add(configlist[i])
        }
    }
    print("the linenum of createEvent：")
    for (int i = 0; i < createEventlist.size(); i++) {
        def createjson = slurper.parseText(createEventlist[i])
        createEventnumberlist.add(createjson.linenumber)
        print(createjson.linenumber+" ")
    }
    print("\n")

    print("the linenum of sendEvent：")
    for (int i = 0; i < sendEventlist.size(); i++) {
        def sendjson = slurper.parseText(sendEventlist[i])
        sendEventnumberlist.add(sendjson.linenumber)
        print(sendjson.linenumber+" ")
    }
    print("\n")


}

def addcreatEvent(List l, String str, String beforestr, String afterstr, int num){
    str = str.substring(1,str.length()-1)
    l.add("def params" + num + " = null")
    l.add("def signatureResult"+ num + " = null")
    l.add("params" + num + " = [")
    l.add("uri: state.URL,")
    l.add("path: state.PATH,")
    String query="query: [ID0: state.ID0, deviceID: device.id, func: \"sign\""
    query = query+","

    if(str.contains(":")) {
        query = query + str
    }else{
        //name: "${eventParams?.name}", value: "${eventParams?.value}"
        query += "name:\"\${" + str +"?.name}\", value: \"\${" + str +"?.value}\""
    }

    query=query+"]"
    query=query+"\n]"
    l.add(query)
    l.add("signatureResult" + num + " = null")
    l.add("  try {")
    l.add("        httpGet(params" + num + ") { resp ->")
    l.add("        signatureResult" + num + " = resp.data")
    l.add("        log.debug \"response data: \${signatureResult" + num + "}\"")
    l.add("      }\n")
    l.add("} catch (e) {")
    l.add("    log.error \"something went wronge: \$e\"")
    l.add("}")

    String result= ''
    if(str.contains(":")){
        result= beforestr + "createEvent("
        def pos = 0
        if(str.contains("data")){
            int intIndex = str.indexOf("data")
            for (int i=intIndex+3; i< str.length(); i++){
                if (str[i] == "["){
                    pos = i
                    break
                }
            }
            str = str.substring(0,pos+1) + " sign: \"\${signatureResult" + num + "}\" ," + str.substring(pos+1,str.length())
            result = result+str+")"
        }
        else{
            result = result+str
            result = result+","
            result = result+" data: [sign: \"\${signatureResult" + num + "}\"])"
        }
    }else{
        result += "def datavalue" + num +"= ''" + "\n"
        result += "if(" + str +".containsKey('data') ){" + "\n"
        result += "datavalue" +num+ " = " + str +"['data']" + "\n"
        result += "datavalue" +num+ " = \"[\" + datavalue" + num + "+ \",\" +\"sign: \" + \"\${signatureResult" + num +"}\" + \"]\"" + "\n"
        //datavalue = "[" + datavalue + "," +"sign: " + "${signatureResult" + num +"}" + "]"
        result += str
        result += ".put('data',\"\${datavalue" + num +"}\")"+ "\n"
        result += " }else{" + "\n"
        result += "datavalue" + num + "=  \"[sign: \" + \"\${signatureResult" +num+ "}\" +\"]\"" + "\n"
        //datavalue =  "[sign: " + "${signatureResult}" +"]"
        result += str
        result +=".put('data',\"\${datavalue" +num+ "}\")" + "\n"
        //eventParams.put('data',"${datavalue}")
        result += "}"+ "\n"
        result += beforestr + "createEvent(" + str + ")"
        //println(result)
    }
    /*result=result+str
    result=result+","
    result = result+"data: [sign: \"\${signatureResult}\"])"*/
    result += afterstr
    l.add(result)
    //l.add("log.trace \"\$result\"")
    //l.add("return result")
}


def addsendEvent(List l, String str, String beforestr, String afterstr, int num){
    str = str.substring(1,str.length()-1)
    l.add("def params" + num + " = null")
    l.add("def signatureResult"+ num + " = null")
    l.add("log.debug \"id is \${device.id}\"")
    l.add(" params" + num + " = [")
    l.add("        uri: state.URL,")
    l.add("        path: state.PATH,")
    String query="        query: [ID0: state.ID0, deviceID: device.id, func: \"sign\""
    query = query+","

    if(str.contains(":")) {
        query = query + str
    }else{
        //name: "${eventParams?.name}", value: "${eventParams?.value}"
        query += "name:\"\${" + str +"?.name}\", value: \"\${" + str +"?.value}\""
    }

    query=query+"]"
    query=query+"\n\t]"
    l.add(query)
    l.add("signatureResult" + num + " = null")
    l.add("try {")
    l.add("    httpGet(params" + num + ") { resp ->")
    l.add("        signatureResult" + num + " = resp.data")
    l.add("        log.debug \"response data: \${signatureResult" + num + "}\"")
    l.add("    }")
    l.add("} catch (e) {")
    l.add("    log.error \"something went wrong : \$e\"")
    l.add("}")

    //String sendevent = beforestr + "sendEvent("
    /*def pos = 0
    if(str.contains("data")){
        int intIndex = str.indexOf("data")
        for (int i=intIndex+3; i< str.length(); i++){
            if (str[i] == "["){
                pos = i
                break
            }
        }
        str = str.substring(0,pos+1) + " sign: \"\${signatureResult" + num + "}\" ," + str.substring(pos+1,str.length())
        sendevent = sendevent+str+")"
    }
    else{
        sendevent = sendevent+str
        sendevent = sendevent+","
        sendevent = sendevent+" data: [sign: \"\${signatureResult" + num + "}\"])"
    }*/
    String sendevent = ''
    if(str.contains(":")){
        sendevent = beforestr + "sendEvent("
        def pos = 0
        if(str.contains("data")){
            int intIndex = str.indexOf("data")
            for (int i=intIndex+3; i< str.length(); i++){
                if (str[i] == "["){
                    pos = i
                    break
                }
            }
            str = str.substring(0,pos+1) + " sign: \"\${signatureResult" + num + "}\" ," + str.substring(pos+1,str.length())
            sendevent = sendevent + str + ")"
        }
        else{
            sendevent = sendevent + str
            sendevent = sendevent + ","
            sendevent = sendevent + " data: [sign: \"\${signatureResult" + num + "}\"])"
        }
    }else{
        sendevent += "def datavalue" + num +"= ''" + "\n"
        sendevent += "if(" + str +".containsKey('data') ){" + "\n"
        sendevent += "datavalue" +num+ " = " + str +"['data']" + "\n"
        sendevent += "datavalue" +num+ " = \"[\" + datavalue" + num + "+ \",\" +\"sign: \" + \"\${signatureResult" + num +"}\" + \"]\"" + "\n"
        //datavalue = "[" + datavalue + "," +"sign: " + "${signatureResult" + num +"}" + "]"
        sendevent += str
        sendevent += ".put('data',\"\${datavalue" + num +"}\")"+ "\n"
        sendevent += " }else{" + "\n"
        sendevent += "datavalue" + num + "=  \"[sign: \" + \"\${signatureResult" +num+ "}\" +\"]\"" + "\n"
        //datavalue =  "[sign: " + "${signatureResult}" +"]"
        sendevent += str
        sendevent +=".put('data',\"\${datavalue" +num+ "}\")" + "\n"
        //eventParams.put('data',"${datavalue}")
        sendevent += "}"+ "\n"
        sendevent += beforestr + "sendEvent(" + str + ")"
    }
    sendevent += afterstr
    l.add(sendevent)
}

def extractBeforeMessage(msg){
    BeforeTxt = ''
    endFlag = 0
    for (int i = 0; i < msg.length()-11; i++) {
        if (msg.substring(i, i+11) == "createEvent" || msg.substring(i, i+9) == "sendEvent" ) {
            endFlag = i
            break
        }
    }
    if(endFlag > 0){
        BeforeTxt = msg.substring(0, endFlag)
    }
    return BeforeTxt
}

def add(filename,headfile,sourcefile,methodMap,slurper,createEventnumberlist,sendEventnumberlist,createEventlist,sendEventlist,outfile,row){

    def outlist=[]
    def headlist = headfile.readLines()
    for (int i = 0; i < headlist.size(); i++) {
        outlist<<headlist[i]
    }

    def installedlist=[]
    installedlist.add("state.URL = \"http://xxx.free.com\"")
    installedlist.add("state.PATH = \"/work\"")
    installedlist.add("state.ID0 = UUID.randomUUID().toString()")

    installflag = false

    def sourcelist = sourcefile.readLines()
    //first line
    outlist << sourcelist[0]

    long startTime=System.currentTimeMillis()

    def bracket_start = 0
    def bracket_end = 0
    def event_num = 0
    for (int i = 0; i < sourcelist.size(); i++) {

        /*add installed()*/
        if( i + 1 == (methodMap["installed"]) ){
            installflag = true
            for (int j=i; j < sourcelist.size(); j++){
                if ((sourcelist[j].toString().contains("{")) && sourcelist[j].toString().contains("}")){
                    def str = sourcelist[j].toString()
                    outlist << str.substring(0,str.length()-1)
                    for (int k = 0; k < installedlist.size(); k++) {
                        outlist<<installedlist[k]
                    }
                    outlist << "}"
                    sourcelist[j] = ""
                    break
                }
                else if(sourcelist[j].toString().contains("{")){
                    outlist << sourcelist[j]
                    for (int k = 0; k < installedlist.size(); k++) {
                        outlist<<installedlist[k]
                    }
                    sourcelist[j] = ""
                    break
                }
                else {
                    outlist << sourcelist[j]
                    sourcelist[j] = ""
                }
            }
            /*creatEvent*/
        }

        if(createEventnumberlist.indexOf(i+1)!=-1){
            event_num = event_num + 1
            beforeTxt = ""
            afterTxt = ""
            param = ""
            for (int j = 0; j < createEventlist.size(); j++) {
                def tmp = slurper.parseText(createEventlist[j])
                if(tmp.linenumber==i+1){
                    args = tmp.arguments.toString()

                    beforeTxt = extractBeforeMessage(sourcelist[i])
                    afterTxt == ""
                    def str = ""
                    bracket_start = i
                    bracket_end = i
                    for (int k = i; k<sourcelist.size(); k++){
                        str += sourcelist[k]
                        if(isGoodBracket(str)){
                            bracket_end = k
                            break
                        }
                    }
                    str -= sourcelist[bracket_end]
                    if(str == "") {
                        str = sourcelist[bracket_end]
                        afterTxt == ""
                        def aftstr = ""
                        def rightbra = 0
                        def posi = str.indexOf("createEvent")
                        if(posi != -1){
                            for(int k=posi+11; k<str.length(); k++){
                                aftstr +=str[k]
                                if(isGoodBracket(aftstr)){
                                    rightbra = k
                                    afterTxt = str.substring(k+1,str.length())
                                    break
                                }
                            }
                        }
                    }else{
                        for(int k = 1; k <= sourcelist[bracket_end].length(); k++){
                            str += sourcelist[bracket_end].substring(0,k)
                            if(isGoodBracket(str)){
                                afterTxt = sourcelist[bracket_end].substring(k,sourcelist[bracket_end].length())
                                break
                            }
                        }
                    }
                }
            }
            for (int k = bracket_start; k <= bracket_end; k++){
                outlist<<("//"+sourcelist[k])
            }
            //outlist<<("//"+sourcelist[i])
            sourcelist[i]=""
            def addcreateevent=[]
            addcreatEvent(addcreateevent,args,beforeTxt,afterTxt,event_num)
            for (int j = 0; j < addcreateevent.size(); j++) {
                outlist<<addcreateevent[j]
            }
        }
        if(sendEventnumberlist.indexOf(i+1)!=-1){
            event_num = event_num + 1
            //Map args=[:]
            //args = ""
            param = ""
            for (int j = 0; j < sendEventlist.size(); j++) {
                def tmp = slurper.parseText(sendEventlist[j])
                if(tmp.linenumber==i+1){
                    args = tmp.arguments.toString()
                    beforeTxt = extractBeforeMessage(sourcelist[i])
                    afterTxt = ""
                    def str = ""
                    bracket_start = i
                    bracket_end = i
                    for (int k = i; k<sourcelist.size(); k++){
                        str += sourcelist[k]
                        if(isGoodBracket(str)){
                            bracket_end = k
                            break
                        }
                    }
                    str -= sourcelist[bracket_end]

                    if(str == "") {
                        str = sourcelist[bracket_end]
                        afterTxt == ""
                        def aftstr = ""
                        def rightbra = 0
                        def posi = str.indexOf("createEvent")
                        if(posi != -1){
                            for(int k=posi+11; k<str.length(); k++){
                                aftstr +=str[k]
                                if(isGoodBracket(aftstr)){
                                    rightbra = k
                                    afterTxt = str.substring(k+1,str.length())
                                    break
                                }
                            }
                        }
                    }else{
                        for(int k = 1; k <= sourcelist[bracket_end].length(); k++){
                            str += sourcelist[bracket_end].substring(0,k)
                            if(isGoodBracket(str)){
                                afterTxt = sourcelist[bracket_end].substring(k,sourcelist[bracket_end].length())
                                break
                            }
                        }
                    }
                }
            }
            for (int k = bracket_start; k <= bracket_end; k++){
                outlist<<("//"+sourcelist[k])
            }
            sourcelist[i]=""
            def addsendevent=[]
            addsendEvent(addsendevent,args,beforeTxt, afterTxt,event_num)
            for (int j = 0; j < addsendevent.size(); j++) {
                outlist<<addsendevent[j]
            }
        }

        if( i < bracket_start || i > bracket_end)
        {
            outlist<<sourcelist[i]
        }
    }

    if(!installflag){
        outlist << "def installed() {"
        for (int j = 0; j < installedlist.size(); j++) {
            outlist<<installedlist[j]
        }
        outlist << "}"
    }

    long endTime=System.currentTimeMillis()
    long runtime = endTime - startTime
    //createExcel(excelContentMap)
    println(filename+"add time： "+(endTime - startTime)+"ms")
    //getExcel(runtime,row)

    outfile.text=" "
    for (int i = 0; i < outlist.size(); i++) {
        outfile<<outlist[i]<<"\n"
    }
}

/*
def getExcel(value,rowNo){
    def targetFolderPath = "../"
    String excelFileName = targetFolderPath + "ExcelTest.xls"
    HSSFWorkbook work = new HSSFWorkbook(new FileInputStream(excelFileName))
    //HSSFSheet sheet = work.getSheetAt(0)
    HSSFSheet sheet = work.getSheet("ExcelContentSheet")
    HSSFRow row0 = sheet.getRow(rowNo)
    HSSFCell cell1 = row0.createCell(2)
    cell1.setCellValue((String)value)

    FileOutputStream out = null
    out = new FileOutputStream(excelFileName)
    work.write(out)
    out.close()
}*/
