import groovy.json.JsonSlurper
import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook

def getflag(str){
    def position = 0
    def aftertxt = null
    for(int i=0; i<str.length(); i++){
        if(str[i] == "{") position = i
    }
    return position
}

def solveline(sourcefile,keyline){
    def lineno = 0
    sourcefile.eachLine { line ->
        lineno++
        for(int i=0; i<keyline.size();i++){
            if( (keyline[i] == lineno) && (!line.contains("{")) ){
                keyline[i] = keyline[i] + 1
            }
        }
    }
}


def isMethod(method,configlist,slurper) {
    for (int i = 0; i < configlist.size(); i++) {
        def json = slurper.parseText(configlist[i])
        if (json.type == "MethodNode") {
            if(json.name == method){
                keyconfig = i
                return true
            }
        }
    }
    return false
}

def extractBeforeMessage(msg){
    def BeforeTxt = ''
    def endFlag = 0
    for (int i = 0; i < msg.length()-11; i++) {
        if (msg.substring(i, i+9) == "sendEvent" ) {
            endFlag = i
            break
        }
    }
    if(endFlag > 0){
        BeforeTxt = msg.substring(0, endFlag)
    }
    return BeforeTxt
}
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
def dealargs(String[] str){
    def index = 0
    def map = [:]
    for(int i =index;i<str.size();i++){
        def temp = str[i]
        //childDevice.sendEvent(name: "color", value: colorUtil.hslToHex((device.color.hue / 3.6) as int, (device.color.saturation * 100) as int))
        //
        if(temp.contains("(")){
            if(!isGoodBracket(temp)){
                for(int j = i +1 ;j<str.size();j++){
                    temp = temp + str[j]
                    i++
                    if(isGoodBracket(temp)) break;
                }
            }
        }
        if(temp.contains(":"))
        {
            field = temp.split(":").getAt(0).trim()
            value = temp.split(":").getAt(1).trim()
            map.put(field,value)
        }
        //println("str:"+str[i]+"  filed: "+filed+"  value: "+value)
        //map.put(field,value)
        //println(map)
    }
    //println("filed: "+map.get('name')+"  value: "+map.get('value'))
    return map
}

def addinslist(flag){
    def installedlist=[]
    installedlist.add("state.URL = \"http://xxx.free.com\"")
    installedlist.add("state.PATH = \"/work\"")
    installedlist.add("state.ID_handler = UUID.randomUUID().toString()")

    def installedlist_sun=[]
    installedlist_sun.add("state.lastEventTime = null")
    installedlist_sun.add("state.timePeriod = 5")

    def installedlist_time=[]
    installedlist_time.add("state.timePeriod = 5")
    installedlist_time.add("state.lastsunrise = null")
    installedlist_time.add("state.lastsunset = null")
    installedlist_time.add("state.LastsunsetTime = null")
    installedlist_time.add("state.LastsunriseTime = null")

    if(flag.contains(0)){
        /*for (int j = 0; j < installedlist.size(); j++) {
            output<<installedlist[j]
        }*/
        return installedlist
    }
    if(flag.contains(3) || flag.contains(4)){
        /*for (int j = 0; j < installedlist_sun.size(); j++) {
            output<<installedlist_sun[j]
        }*/
        return installedlist_sun
    }
    if(flag.contains(5) || flag.contains(6)){
        /*for (int j = 0; j < installedlist_time.size(); j++) {
            output<<installedlist_time[j]
        }*/
        return installedlist_time
    }
}

def addinitlist(flag){
    def init_time=[]
    init_time.add("subscribe(location, \"position\", handlerAdded)")
    init_time.add("subscribe(location, \"sunset\", handlerAdded)")
    init_time.add("subscribe(location, \"sunrise\", handlerAdded)")

    if(flag.contains(5) || flag.contains(6)){
        /* for (int j = 0; j < init_time.size(); j++) {
             outlist<<init_time[j]
         }*/
        return init_time
    }
}

def initSource(headfile,headfile_sun,sourcefile,outfile,eventflag,keyline,notduplicate,installedLinenumber,time_init_num,rownum,childEventlist,childLine){
    def OpPath = "../"
    def sourcelist = sourcefile.readLines()
    def addlist
    def headlist = headfile.readLines()
    def headlist_sun = headfile_sun.readLines()
    def outlist = []
    installflag = false
    initflag = false
    def event_num = 0

    if(eventflag.contains(0)){
        for (int i = 0; i < headlist.size(); i++) {
            outlist<<headlist[i]
        }
    }
    if(eventflag.contains(3) || eventflag.contains(4) || eventflag.contains(5) || eventflag.contains(6)){
        for (int i = 0; i < headlist_sun.size(); i++) {
            outlist<<headlist_sun[i]
        }
    }

    for (int i = 0; i < sourcelist.size(); i++) {
        if( i +1 ==  installedLinenumber[0] ) {
            installflag = true
            for (int j = i; j < sourcelist.size(); j++) {
                if ((sourcelist[j].toString().contains("{")) && sourcelist[j].toString().contains("}")) {
                    def str = sourcelist[j].toString()
                    outlist << str.substring(0, str.length() - 1)
                    returnlist = addinslist(eventflag)
                    if (returnlist != null){
                        for (int k = 0; k < returnlist.size(); k++) {
                            outlist<< returnlist[k]
                        }
                    }
                    outlist << "}"
                    sourcelist[j] = ""
                    break
                } else if (sourcelist[j].toString().contains("{")) {
                    outlist << sourcelist[j]
                    returnlist = addinslist(eventflag)
                    if (returnlist != null){
                        for (int k = 0; k < returnlist.size(); k++) {
                            outlist<< returnlist[k]
                        }
                    }
                    sourcelist[j] = ""
                    break
                } else {
                    outlist << sourcelist[j]
                    sourcelist[j] = ""
                }
            }
        }

        if(i +1 == time_init_num[0] ){
            initflag = true
            for (int j = i; j < sourcelist.size(); j++) {
                if ((sourcelist[j].toString().contains("{")) && sourcelist[j].toString().contains("}")) {
                    def str = sourcelist[j].toString()
                    outlist << str.substring(0, str.length() - 1)
                    returninitlist = addinitlist(eventflag)
                    if (returninitlist != null){
                        for (int k = 0; k < returninitlist.size(); k++) {
                            outlist<< returninitlist[k]
                        }
                    }
                    outlist << "}"
                    sourcelist[j] = ""
                    break
                } else if (sourcelist[j].toString().contains("{")) {
                    outlist << sourcelist[j]
                    returninitlist = addinitlist(eventflag)
                    if (returninitlist != null){
                        for (int k = 0; k < returninitlist.size(); k++) {
                            outlist<< returninitlist[k]
                        }
                    }
                    sourcelist[j] = ""
                    break
                } else {
                    outlist << sourcelist[j]
                    sourcelist[j] = ""
                }
            }
        }

        //TODO child
        def bracket_start = 0
        def bracket_end = 0
        slurper = new JsonSlurper()
        if(childLine.indexOf(i+1)!=-1){
            event_num = event_num + 1
            param = ""
            def idx = childLine.indexOf(i+1)
            def tmp = slurper.parseText(childEventlist[idx])
            if(tmp.linenumber==i+1 && tmp.type == "sendEvent_v1"){
                outlist<<("//"+sourcelist[i])
                if(childLine.indexOf(tmp.linenumber+1)!=-1){
                    def N = tmp.linenumber
                    outlist<<("//"+sourcelist[N])
                    outlist<<("\t\tif("+tmp.flag+"){")
                    addlist0 = []
                    def args0 = tmp.arguments.toString().trim()
                    //delete()
                    if(args0[0] == '[') {
                        args0 = args0.substring(1,args0.length()-1)
                    }
                    String[] tempargs0  = args0.split(",")
                    after = ''
                    addlist0 = addchild(tmp.obj,tempargs0,args0,after,event_num)

                    for (int j = 0; j < addlist0.size(); j++) {
                        outlist<<addlist0[j]
                    }

                    outlist<<("\t\t}else{")

                    addlist1 = []
                    def tmp1 = slurper.parseText(childEventlist[idx+1])
                    def args1 = tmp1.arguments.toString().trim()

                    if(args1[0] == '[') {
                        args1 = args1.substring(1,args1.length()-1)
                    }
                    String[] tempargs1  = args1.split(",")

                    def beforeobj = extractBeforeMessage(sourcelist[N])
                    after = ''
                    addlist1 = addchild( beforeobj ,tempargs1,args1,after,event_num)
                    for (int j = 0; j < addlist1.size(); j++) {
                        outlist<<addlist1[j]
                    }

                    outlist<<("}")
                    temp = childLine.indexOf(N+1)
                    childLine.remove(temp)
                    childEventlist.remove(temp)
                }
            }
            if(tmp.linenumber==i+1 && tmp.type == "sendEvent"){
                def args = tmp.arguments.toString()
                def beforeTxt = extractBeforeMessage(sourcelist[i])
                def afterTxt = ""
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
                    def posi = str.indexOf("sendEvent")
                    if(posi != -1){
                        for(int k=posi+9; k<str.length(); k++){
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

                args = args.substring(1,args.length()-1)
                args = args.trim()
                if(args[0] == '[') {
                    args = args.substring(1,args.length()-1)
                }
                String[] tempargs  = args.split(",")
                def addchildevent=[]
                if(beforeTxt.toString().contains(".")) {
                    for (int k = bracket_start; k <= bracket_end; k++) {
                        outlist << ("//" + sourcelist[k])
                    }
                    sourcelist[i] = ""
                    addchildevent = addchild(beforeTxt,tempargs,args,afterTxt,event_num)
                }
                for (int j = 0; j < addchildevent.size(); j++) {
                    outlist<<addchildevent[j]
                }
            }
        }

        for(int k=0; k<keyline.size(); k++) {
            if (i == keyline[k] -1 && notduplicate[k] != 0 && eventflag[k] !=7) {
                switch (eventflag[k]) {
                    case 0:
                        addfile = new File(OpPath + "configFile/app_device.groovy")
                        break
                    case 1:
                        addfile = new File(OpPath + "configFile/app_mode.groovy")
                        break
                    case 2:
                        addfile = new File(OpPath + "configFile/app_position.groovy")
                        break
                    case 3:
                        addfile = new File(OpPath + "configFile/app_sunrise.groovy")
                        break
                    case 4:
                        addfile = new File(OpPath + "configFile/app_sunset.groovy")
                        break
                    case 5:
                        addfile = new File(OpPath + "configFile/app_suntime.groovy")
                        break
                    case 6:
                        addfile = new File(OpPath + "configFile/app_suntime.groovy")
                        break
                /*default:
                    addfile = new File(OpPath + "configFile/app_device.groovy")*/
                }
                addlist = addfile.readLines()
                for (int j=i; j < sourcelist.size(); j++){
                    if ((sourcelist[j].toString().contains("{")) && sourcelist[j].toString().contains("}")){
                        def pos = getflag(sourcelist[j])
                        def beforestr = sourcelist[j].toString().substring(0,pos+1)
                        def afterstr = sourcelist[j].toString().substring(pos+1,sourcelist[j].toString().length())

                        outlist << beforestr
                        for (int l = 0; l < addlist.size(); l++) {
                            outlist << addlist[l]
                        }
                        outlist << afterstr
                        sourcelist[j] = ""
                        break
                    }
                    else if(sourcelist[j].toString().contains("{") ){
                        def pos = getflag(sourcelist[j])
                        if( pos == sourcelist[j].length()-1){
                            outlist << sourcelist[j]
                            for (int l = 0; l < addlist.size(); l++) {
                                outlist << addlist[l]
                            }
                            sourcelist[j] = ""
                            break
                        }
                        else{
                            def beforestr = sourcelist[j].toString().substring(0,pos+1)
                            def afterstr = sourcelist[j].toString().substring(pos+1,sourcelist[j].toString().length())
                            outlist << beforestr
                            for (int l = 0; l < addlist.size(); l++) {
                                outlist << addlist[l]
                            }
                            outlist << afterstr
                            sourcelist[j] = ""
                            break
                        }
                    }
                    else {
                        outlist << sourcelist[j]
                        sourcelist[j] = ""
                    }
                }
            }
        }
        outlist<<sourcelist[i]
    }

    if(eventflag.contains(5) || eventflag.contains(6)){
        timefile = new File(OpPath+"configFile/app_suntime_function.groovy")
        def timelist = timefile.readLines()
        for (int j = 0; j < timelist.size(); j++) {
            outlist<<timelist[j]
        }
    }

    if(!installflag){
        outlist << "def installed() {"
        returnlist = addinslist(eventflag)
        if (returnlist != null){
            for (int k = 0; k < returnlist.size(); k++) {
                outlist<< returnlist[k]
            }
        }
        outlist << "}"
    }
    if(!initflag){
        outlist << "def initialize() {"
        reinitlist = addinitlist(eventflag)
        if (reinitlist != null) {
            for (int k = 0; k < reinitlist.size(); k++) {
                outlist << reinitlist[k]
            }
        }
        outlist << "}"
    }


    outfile.text=""
    for (int i = 0; i < outlist.size(); i++) {
        outfile<<outlist[i]<<"\n"
    }

}

def getKey(configlist,slurper,eventflag,beginnum,keyline,notduplicate,installedLinenumber,time_init_num,childEventlist,childLine) {
    for (int i = 0; i < configlist.size(); i++) {
        def json = slurper.parseText(configlist[i])
        if (json.type == "subscribe") {
            //subscribe app or location
            if(json.arguments.size <= 1) {
                continue
            }
            /*if(json.arguments.size == 2 && json.arguments[0] == "location") {
                eventflag[beginnum]= 1
            }*/
            if(json.arguments.size >= 2 && json.arguments[0] == "location"){
                if(json.arguments[1] == "mode" || json.arguments[1] == "null" ){
                    eventflag[beginnum]= 1
                }
                else if(json.arguments[1] == "position"){
                    eventflag[beginnum]= 2
                }
                else if(json.arguments[1] == "sunrise"){
                    eventflag[beginnum]= 3
                }
                else if(json.arguments[1] == "sunset"){
                    eventflag[beginnum]= 4
                }
                else if(json.arguments[1] == "sunriseTime"){
                    eventflag[beginnum]= 5
                }
                else if(json.arguments[1] == "sunsetTime"){
                    eventflag[beginnum]= 6
                }else {
                    //arguments":["location"," null"," locationHandler"," [filterEvents:false]
                    eventflag[beginnum]= 1
                }
            }else if(json.arguments.size == 2 && json.arguments[0] == "app"){
                eventflag[beginnum]= 7
            }
            else{
                eventflag[beginnum]= 0
            }
            for (int j = 0; j < json.arguments.size(); j++) {
                if(isMethod(json.arguments[j],configlist,slurper)) {
                    def methodjson = slurper.parseText(configlist[keyconfig])
                    keyline[beginnum] = methodjson.linenumber
                }
            }
            beginnum++
        }
        else if(json.name=="installed"){
            installedLinenumber[0] = json.linenumber
        }else if(json.name=="initialize"){
            time_init_num[0] = json.linenumber
        }
        //TODO child
        else if(json.type=="sendEvent" || json.type=="sendEvent_v1"){
            childEventlist.add(configlist[i])
            childLine.add(json.linenumber)
        }
    }

    for (int i=0; i<beginnum; i++){
        for (int j=i+1; j<beginnum; j++){
            if((keyline[i] == keyline[j]) && ((eventflag[i]==eventflag[j]) || (eventflag[i]==5 && eventflag[j]==6) ||(eventflag[i]==6 && eventflag[j]==5))){
                notduplicate[j] = 0
            }
        }
    }
}

def addchild(obj,temp,args,afterstr,num){
    /*if(beforeTxt.toString().contains("."))
    {
        for (int k = bracket_start; k <= bracket_end; k++){
            outlist<<("//"+sourcelist[k])
        }
        sourcelist[i]=""*/
    def returnlist = []
    if(temp.size()==1) {
        returnlist.add("\t\tdef signatureResult"+num+"= null")
        returnlist.add("\t\tsignatureResult"+num+" = "+obj+"getSignature("+args+".name,"+args+".value)")
        returnlist.add(args+".put('data',[sign: \"\${signatureResult"+num+"}\"])")
        returnlist.add(obj+"sendEvent("+args+")"+afterstr)
    }else{
        def eventmap = dealargs(temp)
        def filedname = eventmap.get("name")
        if(eventmap.get("name") == null)  filedname = eventmap.get("\"name\"")
        def filedvalue = eventmap.get("value")
        if(eventmap.get("value") == null)  filedvalue = eventmap.get("\"value\"")
        returnlist.add("\t\tdef signatureResult"+num+"= null")
        returnlist.add("\t\tsignatureResult"+num+" = "+obj+"getSignature("+filedname+","+filedvalue+")")
        returnlist.add(obj+"sendEvent("+args+",data: [sign: \"\${signatureResult"+num+"}\"]"+")"+afterstr)
    }
    return returnlist
    //}
    //		sendEvent( dni, [name: "power",value:0.0] ) More investigation is needed.
    /*else if(!tempargs[0].contains(":")){
        if(tempargs.size()==2) {
            addchildevent.add("def signatureResult"+event_num+"= null")
            addchildevent.add("signatureResult"+event_num+" = "+beforeTxt+"getSignature("+tempargs[1]+".name,"+tempargs[1]+".value)")
            addchildevent.add(tempargs[1]+".put('data',[sign: \"\${signatureResult"+event_num+"}\"])")
            addchildevent.add(beforeTxt+"sendEvent("+tempargs[0]+","+tempargs[1]+")")
        }else{
            println(tempargs)
            def eventmap = dealargs(tempargs,1)
            println(eventmap)
            def filedname = eventmap.get("name")
            def filedvalue = eventmap.get("value")
            addchildevent.add("def signatureResult"+event_num+"= null")
            addchildevent.add("signatureResult"+event_num+" = "+beforeTxt+"getSignature("+filedname+","+filedvalue+")")
            addchildevent.add(beforeTxt+"sendEvent("+args+",data: [sign: \"\${signatureResult"+event_num+"}\"]"+")")
        }
    }*/
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
}
*/