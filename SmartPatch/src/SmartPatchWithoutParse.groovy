import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

OpPath="../"
headfile = new File(OpPath+"configFile/Head.groovy")
headfile_sun = new File(OpPath+"configFile/app_header_sunrise_sunset.groovy")

//deal with comments
def removeCommentsWithQuoteAndDoubleEscape(String code)
{
    StringBuilder sb = new StringBuilder()
    int cnt = 0
    boolean quoteFlag = false
    for (int i = 0; i < code.length(); i++) {
        if(!quoteFlag) {
            if(code.charAt(i) == '\"') {
                sb.append(code.charAt(i))
                quoteFlag = true
                continue
            }
            else if(i+1 < code.length() && code.charAt(i) == '/' && code.charAt(i+1) == '/') {
                while(code.charAt(i) != '\n') {
                    i++
                }
                sb.append(code.charAt(i))
                continue
            }
            else {
                // /**/
                if(cnt == 0) {
                    if(i+1 < code.length() && code.charAt(i) == '/' && code.charAt(i+1) == '*') {
                        cnt++
                        i++
                        continue
                    }
                }
                else {
                    if(i+1 < code.length() && code.charAt(i) == '*' && code.charAt(i+1) == '/') {
                        cnt--
                        i++
                        continue
                    }

                    if(i+1 < code.length() && code.charAt(i) == '/' && code.charAt(i+1) == '*') {
                        cnt++
                        i++
                        continue
                    }
                }

                if(cnt == 0) {
                    sb.append(code.charAt(i))
                    continue
                }
            }
        }

        else
        {
            if(code.charAt(i) == '\"' && code.charAt(i-1) != '\\') {
                sb.append(code.charAt(i))
                quoteFlag = false
            }

            else {
                sb.append(code.charAt(i))
            }
        }
    }
    return sb.toString()
}

def ReadFileToString(String FilePath)
{
    FileInputStream fis = null
    BufferedReader br = null
    try
    {
        fis = new FileInputStream(FilePath)
        br = new BufferedReader(new InputStreamReader(fis, "utf-8"))
    }
    catch (IOException e)
    {
        e.printStackTrace()
    }
    StringBuffer sb = new StringBuffer()
    String temp = null
    try
    {
        while((temp = br.readLine()) != null)
        {
            sb.append(temp+'\n')
        }
    } catch (IOException e)
    {
        e.printStackTrace()
    }
    return sb.toString()
}

appentity = new addappfunc_notime();
appdir = new File("../sourceFile/srcAPP")
if (appdir !=null && appdir.exists()&& appdir.isDirectory()){
    File[] files = appdir.listFiles()
    if(files !=null && files.length > 0){
        for (int i = 0; i < files.size(); i++){
            String [] sz=files[i].name.split("/")
            filename = sz[sz.length-1].minus(".groovy")

            appfilepath = "../sourceFile/srcAPP/"+filename+".groovy"
            appsourcefile = new File(appfilepath)
            appoutputpath =  "../sourceFile/testcantAPP/"+filename+".groovy"
            appoutputfile = new File(appoutputpath)

            if(appoutputfile.exists() && appoutputfile.isFile()){
                appoutputfile.delete()
            }
            appoutputfile.createNewFile()
            String str = ReadFileToString(appfilepath)
            appoutputfile << removeCommentsWithQuoteAndDoubleEscape(str)

            after_appsourcefile = new File(appoutputpath)
            appconfigpath =  "../configFile/appcantConfig/"+filename+"_config.txt"
            appconfigfile = new File(appconfigpath)
            appconfigfile.createNewFile()
            generate(after_appsourcefile,appconfigfile)

            slurper = new JsonSlurper()
            keyconfig=0
            keyline=[]
            eventflag=[]
            notduplicate=[]
            beginnum=0
            installedLinenumber=0
            time_init_num=0

            outputpath = "../outFile/out_cant_APP/"+filename+"_out.txt"
            outfile = new File(outputpath)
            if(outfile.exists() && outfile.isFile()){
                outfile.delete()
            }
            outfile.createNewFile()
            configlist = appconfigfile.collect {it}
            appentity.getKey(configlist,slurper,eventflag,beginnum,keyline,notduplicate)
            appentity.solveline(after_appsourcefile,keyline)
            appentity.initSource(headfile,headfile_sun,after_appsourcefile,outfile,eventflag,keyline,notduplicate,installedLinenumber,time_init_num)

            println(filename+" done")
        }
    } else{
        println("no file exists")
    }
}
else {
    println("there is no such directory")
}

deventire = new adddevfunc_notime();
devdir = new File("../sourceFile/srcDevice")
if (devdir !=null && devdir.exists()&& devdir.isDirectory()){
    File[] files = devdir.listFiles()
    if(files !=null && files.length > 0){
        for (int i = 0; i < files.size(); i++){
            String [] sz=files[i].name.split("/")
            filename = sz[sz.length-1].minus(".groovy")

            devfilepath = "../sourceFile/srcDevice/"+filename+".groovy"
            devsourcefile = new File(devfilepath)
            devoutputpath =  "../sourceFile/testcantDevice/"+filename+".groovy"
            devoutputfile = new File(devoutputpath)

            if(devoutputfile.exists() && devoutputfile.isFile()){
                devoutputfile.delete()
            }
            devoutputfile.createNewFile()
            String str = ReadFileToString(devfilepath)
            devoutputfile << removeCommentsWithQuoteAndDoubleEscape(str)


            after_devsourcefile = new File(devoutputpath)
            devconfigpath =  "../configFile/devicecantConfig/"+filename+"_config.txt"
            devconfigfile = new File(devconfigpath)
            if(devconfigfile.exists() && devconfigfile.isFile()){
                devconfigfile.delete()
            }
            devconfigfile.createNewFile()
            generate(after_devsourcefile,devconfigfile)

            configlist = devconfigfile.collect {it}
            slurper = new JsonSlurper()
            methodMap=[:]
            createEventlist=[]
            sendEventlist=[]
            createEventnumberlist=[]
            sendEventnumberlist=[]

            outputpath = "../outFile/out_cant_device/"+filename+"_out.txt"
            outfile = new File(outputpath)
            deventire.getconfig(configlist,slurper,methodMap,sendEventlist,createEventlist,createEventnumberlist,sendEventnumberlist)
            deventire.add(filename,headfile,after_devsourcefile,methodMap,slurper,createEventnumberlist,sendEventnumberlist,createEventlist,sendEventlist,outfile)
            println(filename+" done")
        }
    } else{
        println("no file exists")    }
}
else {
    println("there is no such directory")
}

def stringReplace(str) {
    String restr= str.replace("\"", "")
    return restr
}

def extractMessage(msg){
    SaveTxt = null
    startFlag = 0
    for (int i = 0; i < msg.length()-1; i++) {
        if (msg.charAt(i) == '(') {
            startFlag = i
            break
        }
    }
    SaveTxt = msg.substring(startFlag+1, msg.length()-1)
    return SaveTxt
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

def extractParam(file,linenum){
    def SaveTxt = ''
    def str = ""
    def sourcelist = file.readLines()
    for (int i=linenum-1; i<sourcelist.size(); i++){
        def linestr = sourcelist[i].toString()
        str += linestr
        if (isGoodBracket(str)){
            for (int j = 0; j < str.length()-11; j++) {
                if (str.substring(j, j+11) == "createEvent") {
                    SaveTxt = str.substring(j+11,str.length())
                    return SaveTxt.trim()
                }
                if (str.substring(j, j+9).toString() == "sendEvent") {
                    SaveTxt = str.substring(j+9,str.length())
                    return SaveTxt.trim()
                }
            }
            break
        }
    }
}


def findline(sourcefile,name){
    lineno = 0
    flag = 0
    sourcefile.eachLine { line ->
        lineno++
        //str = "def "+ name
        def len = name.length()
        if(line.contains("def") && line.contains(name)){
            for (int i = 0; i < line.length()-name.length(); i++) {
                if (line.substring(i, i+name.length()) == name) {
                    def str = line.substring(i+name.length(), line.length()).trim()
                    //println(lineno + line)
                    if(str == null || str[0] == "("){
                        flag = lineno
                    }
                }
            }
            //flag = lineno
        }
    }
    return flag
}

def generate(sourcefile,output){
    linenum=0
    //def regex = '.*subscribe[(].*'
    def init_regex = '.*def initialize.*'
    def ins_regex = '.*def installed.*'
    def createregex = '.*createEvent.*'
    def sendregex = '.*sendEvent.*'
    List jsonlist = []
    sourcefile.eachLine { line ->
        linenum++
        if (line.trim().length()>=2 &&line.trim().substring(0, 2) != "//"){
            if (line.matches(ins_regex)) {
                def ins_json = new JsonBuilder()
                ins_json {
                    type "MethodNode"
                    name "installed"
                    linenumber linenum
                }
                jsonlist.add(ins_json)
            }
            if (line.matches(init_regex)) {
                def init_json = new JsonBuilder()
                init_json {
                    type "MethodNode"
                    name "initialize"
                    linenumber linenum
                }
                jsonlist.add(init_json)
            }
            if (line.contains("subscribe")) {
                def param = null
                param = extractMessage(line)
                if (param != null && param.length() != 0) {
                    List arglist = []
                    arglist = param.split(',');
                    for (int j = 0; j < arglist.size(); j++) {
                        if (arglist[j].contains("\"")) {
                            arglist[j] = stringReplace(arglist[j].toString().trim())
                        }
                        //println(arglist[j])
                    }
                    def json = new JsonBuilder()
                    json {
                        type "subscribe"
                        linenumber linenum
                        arguments arglist
                    }
                    jsonlist.add(json)

                    def funcline = 0
                    def namestr = ""
                    def json_func = new JsonBuilder()

                    if (arglist.size() == 1) {
                        funcline = 0
                    } else if (arglist.size() == 2 && arglist[0] == "location") {
                        funcline = findline(sourcefile,arglist[1].toString().trim())
                        namestr = arglist[1]
                    } else if (arglist.size() >= 3) {
                        funcline = findline(sourcefile,arglist[2].toString().trim())
                        namestr = arglist[2]
                    } else {
                        funcline = 0
                    }
                    json_func {
                        type "MethodNode"
                        name namestr
                        linenumber funcline
                    }
                    jsonlist.add(json_func)
                }
            }
            if (line.matches(createregex)) {
                def param = extractParam(sourcefile, linenum)
                if (param.length() != 0) {
                    //param = param.replace(" ","")
                    //去除()
                    param = param.substring(1, param.length() - 1)
                    List arglist = []
                    arglist = param.split(',')
                    def json = new JsonBuilder()
                    json {
                        type "createEvent"
                        linenumber linenum
                        arguments arglist
                    }
                    jsonlist.add(json)
                }
            }
            if (line.matches(sendregex)) {
                def param = extractParam(sourcefile, linenum)
                if (param.length() != 0) {
                    param = param.substring(1, param.length() - 1)
                    List arglist = []
                    arglist = param.split(',')
                    def json = new JsonBuilder()
                    json {
                        type "sendEvent"
                        linenumber linenum
                        arguments arglist
                    }
                    jsonlist.add(json)
                }
            }
        }
    }

    for (int k = 0; k < jsonlist.size(); k++) {
        output << jsonlist[k] << "\n"
    }
}


