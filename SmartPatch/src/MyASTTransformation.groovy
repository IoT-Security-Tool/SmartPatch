import groovy.json.JsonBuilder
import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.ConstructorNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.GroovyClassVisitor
import org.codehaus.groovy.ast.PropertyNode
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.EmptyExpression
import org.codehaus.groovy.ast.expr.MapEntryExpression
import org.codehaus.groovy.ast.expr.MapExpression
import org.codehaus.groovy.ast.expr.MethodCall
import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.NamedArgumentListExpression
import org.codehaus.groovy.ast.expr.TernaryExpression
import org.codehaus.groovy.ast.expr.TupleExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.CaseStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.stmt.ForStatement
import org.codehaus.groovy.ast.stmt.IfStatement
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.stmt.ReturnStatement
import org.codehaus.groovy.ast.stmt.SwitchStatement
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.macro.matcher.ASTMatcher
import org.codehaus.groovy.transform.ASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation
import java.lang.System
import java.util.regex.Matcher


@GroovyASTTransformation
class MyASTTransformation implements ASTTransformation {

    List jsonlist = []
    def OpPath = "../"
    def filename=""
    def filePath = ""

    boolean initFile(SourceUnit source){
        if (source.name.indexOf("sourceFile")==-1) {
            return false
        }

        //get filename
        String[] sz = source.name.split("\\\\");
        filename = sz[sz.length-1].minus(".groovy")

        filePath = OpPath + "configFile/allconfig//"+filename+"_config.txt"

        return true
    }

    void dealExpressionStatement(statement,src){
        /**
         * Expression has BinaryExpression, ArgumentListExpression, etc.
         * We mainly focus on MethodCallExpression
         * statement includes ReturnStatement, etc.
         */

        if (statement.getExpression().getClass().getSimpleName() == "MethodCallExpression") {
            MethodCall sub = statement.getExpression()
            //println(sub.methodAsString) childDevice.sendEvent
            if (sub.methodAsString != "subscribe" && sub.methodAsString != "createEvent" && sub.methodAsString != "sendEvent" ){
                for(int i=0; i<sub.getArguments().size(); i++){
                    //class org.codehaus.groovy.ast.expr.ClosureExpression
                    //class org.codehaus.groovy.ast.expr.GStringExpression
                    //class org.codehaus.groovy.ast.expr.ConstantExpression
                    //println(arg.getExpression(i).class)
                    if(sub.getArguments().getExpression(i).class.toString() == "class org.codehaus.groovy.ast.expr.ClosureExpression" ){
                        BlockStatement block = sub.getArguments().getExpression(i).getCode() as BlockStatement
                        dealBlockStatement(block,src)
                    }
                }
            }
            if (sub.methodAsString == "subscribe" ) {
                ArgumentListExpression arg = sub.getArguments()
                //  file<<"subscribe "<<statement.lineNumber<<" "<<arg.size()<<" "
                List arglist = []
                for (int j = 0; j < arg.size(); j++) {
                    arglist.add(arg.getExpression(j).getText().toString())
                }
                def json = new JsonBuilder()
                json {
                    type "subscribe"
                    linenumber statement.lineNumber
                    arguments arglist
                }
                jsonlist.add(json)
            } else if (sub.methodAsString == "createEvent" ) {
                def lineno = statement.lineNumber
                addcreatejson(src,lineno)
            } else if (sub.methodAsString == "sendEvent") {
                def lineno = statement.lineNumber
                addsendjson(src,lineno)
                /*def param = ""
                param = processparam(sub,param)
                def json = new JsonBuilder()
                json {
                    type "sendEvent"
                    linenumber statement.lineNumber
                    arguments param
                }
                jsonlist.add(json)*/
            }
        }
        //else if(statement.getExpression().getRightExpression().getClass().getSimpleName() ==  "MethodCallExpression"){
        else if(statement.getExpression().getClass().getSimpleName() == "BinaryExpression" ||
                statement.getExpression().getClass().getSimpleName() == "DeclarationExpression" ) {
            if(statement.getExpression().getRightExpression().getClass().getSimpleName() == "MethodCallExpression"){
                if (statement.getExpression().getRightExpression().methodAsString == "subscribe" ) {
                    ArgumentListExpression arg = statement.getExpression().getRightExpression().getArguments()
                    //  file<<"subscribe "<<statement.lineNumber<<" "<<arg.size()<<" "
                    List arglist = []
                    for (int j = 0; j < arg.size(); j++) {
                        arglist.add(arg.getExpression(j).getText().toString())
                    }
                    def json = new JsonBuilder()
                    json {
                        type "subscribe"
                        linenumber statement.lineNumber
                        arguments arglist
                    }
                    jsonlist.add(json)
                }else if (statement.getExpression().getRightExpression().methodAsString == "createEvent") {
                    def right = statement.getExpression().getRightExpression()
                    def lineno = statement.lineNumber
                    addcreatejson(src,lineno)
                }else if(statement.getExpression().getRightExpression().methodAsString == "sendEvent"){
                    def right = statement.getExpression().getRightExpression()
                    def lineno = statement.lineNumber
                    addsendjson(src,lineno)
                }
            }else if(statement.getExpression().getRightExpression().getClass().getSimpleName() == "TernaryExpression"){
                TernaryExpression te = statement.getExpression().getRightExpression()
                if(te.getTrueExpression().getClass().getSimpleName() == "MethodCallExpression" ){
                    if (te.getTrueExpression().methodAsString == "createEvent") {
                        def tes = te.getTrueExpression()
                        def lineno = statement.lineNumber
                        addcreatejson(src,lineno)
                    }else if(te.getTrueExpression().methodAsString == "sendEvent"){
                        def tes = te.getTrueExpression()
                        def lineno = statement.lineNumber
                        addsendjson(src,lineno)
                    }
                }
            }
         }else{
            if(statement.getExpression().getClass().getSimpleName() == "TernaryExpression"){
                if(statement.getExpression().getTrueExpression().getClass().getSimpleName() == "MethodCallExpression" ){
                    if (statement.getExpression().getTrueExpression().methodAsString == "createEvent") {
                        def tes = statement.getExpression().getTrueExpression()
                        def lineno = statement.lineNumber
                        addcreatejson(src,lineno)
                    }else if(statement.getExpression().getTrueExpression().methodAsString == "sendEvent"){
                        def tes = statement.getExpression().getTrueExpression()
                        def lineno = statement.lineNumber
                        addsendjson(src,lineno)
                    }
                }
            }
        }
    }

    void dealIfStatement(IfStatement statement,src){
        if(statement.getIfBlock().getClass().getSimpleName()=="BlockStatement"){
            BlockStatement codeIf = statement.getIfBlock()
            dealBlockStatement(codeIf,src)
        }else if(statement.getIfBlock().getClass().getSimpleName()=="IfStatement") {
            IfStatement codeIf = statement.getIfBlock() as IfStatement
            dealIfStatement(codeIf,src)
        }else if(statement.getIfBlock().getClass().getSimpleName()=="ExpressionStatement"){
            ExpressionStatement codeexp = statement.getIfBlock() as ExpressionStatement
            dealExpressionStatement(codeexp,src)
        }
        else{
            if(statement.getIfBlock().getClass().getSimpleName()!="EmptyStatement"){
                print("if unresolved Expression:")
                println(statement.getIfBlock().getClass().getSimpleName())
            }
        }

        if(statement.getElseBlock().getClass().getSimpleName()=="BlockStatement"){
            BlockStatement codeElse = statement.getElseBlock()
            dealBlockStatement(codeElse,src)
        }
        else if(statement.getElseBlock().getClass().getSimpleName()=="IfStatement") {
            IfStatement state = statement.getElseBlock() as IfStatement
            dealIfStatement(state,src)
        }else if(statement.getIfBlock().getClass().getSimpleName()=="ExpressionStatement"){
            ExpressionStatement codeexp = statement.getIfBlock() as ExpressionStatement
            dealExpressionStatement(codeexp,src)
        }
        else{
            if(statement.getElseBlock().getClass().getSimpleName()!="EmptyStatement"){
                print("else unresolved Expression:")
                println(statement.getElseBlock().getClass().getSimpleName())
            }
        }

    }

    void dealReturnStatement(ReturnStatement statement,src){
        if (statement.getExpression().getClass().getSimpleName() == "TernaryExpression"){
          dealExpressionStatement(statement,src)
        }
    }

    void dealBlockStatement(BlockStatement code,src){
        for (int i = 0; i < code.statements.size(); i++) {
            switch (code.statements[i].getClass().getSimpleName()) {
                case "ExpressionStatement":
                    ExpressionStatement statement = code.statements[i] as ExpressionStatement
                    dealExpressionStatement(statement,src)
                    break
                case "IfStatement":
                    IfStatement statement = code.statements[i] as IfStatement
                    dealIfStatement(statement,src)
                    break
                case "SwitchStatement":
                    SwitchStatement statement = code.statements[i] as SwitchStatement
                    for (int j = 0; j < statement.getCaseStatements().size(); j++) {
                        CaseStatement cas = statement.getCaseStatement(j)
                        if(cas.getCode().getClass().getSimpleName() != ""){
                            BlockStatement cascode = cas.getCode() as BlockStatement
                            dealBlockStatement(cas.getCode(),src)
                        }
                    }
                    break
                case "ForStatement":
                    ForStatement fors = code.statements[i] as ForStatement
                    if(fors.getLoopBlock().getClass().getSimpleName() == "IfStatement"){
                        dealIfStatement(fors.getLoopBlock(),src)
                    }
                    if(fors.getLoopBlock().getClass().getSimpleName() == "BlockStatement"){
                        dealBlockStatement(fors.getLoopBlock(),src)
                    }
                    break
                case "ReturnStatement":
                    ReturnStatement rets = code.statements[i] as ReturnStatement
                    dealReturnStatement(rets,src)
                    break
            }

        }
    }


    @Override
    void visit(ASTNode[] nodes, SourceUnit source) {

        if(!initFile(source))return

        File file = new File(filePath)
        file.text = ""

        def sourcepath = source.name.toString()

        println("AST is built up.")
        long startTime=System.currentTimeMillis()
        //println("AST:" + startTime)

        /**
         * Traverse
         * */

        //@Override

        source.AST.classes.each {
            it.visitContents(new GroovyClassVisitor() {
                void visitMethod(MethodNode node) {
                    if (node.name != "main" && node.name != "run") {
                        //Method with parameters
                        if (node.parameters.length != 0) {
                            Parameter[] pa = node.getParameters()
                            def methodArgList = []
                            for (int i = 0; i < pa.length; i++) {
                                methodArgList << pa[i].name
                            }
                            def json = new JsonBuilder()
                            json {
                                type "MethodNode"
                                name node.name
                                linenumber node.lineNumber
                                lastlinenumber node.lastLineNumber
                                arg methodArgList
                            }
                            jsonlist.add(json)
                        } else {
                            def json = new JsonBuilder()
                            json {
                                type "MethodNode"
                                name node.name
                                linenumber node.lineNumber
                                lastlinenumber node.lastLineNumber
                            }
                            jsonlist.add(json)
                        }

                        BlockStatement code = node.getCode() as BlockStatement
                        dealBlockStatement(code,sourcepath)

                    } else if (node.name == "run") {
                        BlockStatement code = node.getCode() as BlockStatement
                        code.statements.clear()
                    }
                }

                @Override
                void visitField(FieldNode node) {
                }
                @Override
                void visitProperty(PropertyNode node) {
                }
                @Override
                void visitClass(ClassNode node) {

                }
                @Override
                void visitConstructor(ConstructorNode node) {

                }

            })
        }

        for (int k = 0; k < jsonlist.size(); k++) {
            file << jsonlist[k] << "\n"
        }

        //output
        println file.text
        println("AST parsing is complete.")
        long endTime=System.currentTimeMillis()
        println(filename+" spends： "+(endTime - startTime)+"ms")
        println()
        //long runtime = endTime - startTime
        //modifyExcel(filename,runtime)

    }

    def addcreatejson(srcfile,num){
        def param = ""
        param = extractParam(srcfile,num)
        def json = new JsonBuilder()
        json {
            type "createEvent"
            linenumber num
            arguments param
        }
        jsonlist.add(json)
    }

    def addsendjson(srcfile,num){
        def param = ""
        //param = processparam(state,param)
        param = extractParam(srcfile,num)
        def json = new JsonBuilder()
        json {
            type "sendEvent"
            linenumber num
            arguments param
        }
        jsonlist.add(json)
    }

    /*def addchildjson(srcfile,num){
        def param = ""
        param = extractParam(srcfile,num)
        def json = new JsonBuilder()
        json {
            type "child"
            linenumber num
            arguments param
        }
        jsonlist.add(json)
    }*/

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
        def SaveTxt = null
        def str = ""
        def sourcefile = new File(file)
        def sourcelist = sourcefile.readLines()
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

    /*
    def modifyExcel(filename, value){
        def targetFolderPath = "../"
        String excelFileName = targetFolderPath + "ExcelTest.xls"
        HSSFWorkbook work = new HSSFWorkbook(new FileInputStream(excelFileName))
        HSSFSheet sheet = work.getSheetAt(0)
        //HSSFSheet sheet = work.getSheet(ExcelContentSheet)
        int rowNo = sheet.getLastRowNum() + 1
        HSSFRow row0 = sheet.createRow(rowNo)
        HSSFCell cell0 = row0.createCell(0)
        cell0.setCellValue((String)filename)
        HSSFCell cell1 = row0.createCell(1)
        cell1.setCellValue((String)value)

        FileOutputStream out = null;
        out = new FileOutputStream(excelFileName);
        work.write(out)
        out.close()
    }
    */

    /*
    //Additional methods for extracting parameters: this works for most simple cases

    def processparam(sub,param){
    //println(sub.getArguments().getText())
    //def str = sub.getArguments().getText()
    //def param = str.substring(1,str.length()-1)
    def str = sub.getArguments().getText()
    if(sub.getArguments().size() == 1 && sub.getArguments().getExpression(0).getClass().getSimpleName() == "MethodCallExpression"){
        param += sub.getArguments().getExpression(0).methodAsString
        param += "("
        param = processparam(sub.getArguments().getExpression(0),param)
        param += ")"
    }else if(sub.getArguments().size() == 1){
        for (int j = 0; j < sub.getArguments().size(); j++) {
            if (sub.getArguments().getExpression(0).type.toString() == "java.lang.String"
                    || sub.getArguments().getExpression(0).type.toString() == "groovy.lang.GString"){
                param = param + "\"" + sub.getArguments().getExpression(0).text.toString() +"\""
            }else if(str.contains(",")){
                //MapExpression
                //NamedArgumentListExpression namedArgumentListExpression = tuple.getExpression(j)

                def maplist = sub.getArguments().getExpression(j).getMapEntryExpressions()
                for (int k = 0; k < maplist.size(); k++) {
                    MapEntryExpression mapEntry = maplist[k]
                    //argmap.put(mapEntry.keyExpression.text, mapEntry.valueExpression.text)
                    //println(mapEntry.valueExpression.type)
                    param = param + mapEntry.keyExpression.text.toString() + ":"
                    if(mapEntry.keyExpression.text.toString() == "data"){
                        def datalist = mapEntry.valueExpression.getMapEntryExpressions()
                        param = param + "["
                        for (int datanum = 0; datanum < datalist.size(); datanum++){
                            MapEntryExpression datamapEntry = datalist[datanum]
                            param = param + datamapEntry.keyExpression.text.toString() + ":"
                            if(datamapEntry.valueExpression.getClass().getSimpleName() == "MethodCallExpression"){
                                def size = datamapEntry.valueExpression.getArguments().size()
                                for(int l=0 ; l<size; l++){
                                    if(datamapEntry.valueExpression.getArguments().getExpression(l).getClass().getSimpleName() == "MapExpression")
                                    {
                                        param += processparam(datamapEntry.valueExpression,param)
                                    }
                                    else{
                                        param = param + datamapEntry.valueExpression.text.toString()
                                    }
                                }
                            }
                            else if(datamapEntry.valueExpression.type.toString() == "java.lang.String"
                                    || datamapEntry.valueExpression.type.toString() == "groovy.lang.GString"){
                                param = param + "\"" + datamapEntry.valueExpression.text.toString() +"\""
                            }else{
                                param = param + datamapEntry.valueExpression.text.toString()
                            }
                            if(datanum != datalist.size()-1){
                                param = param + ","
                            }
                        }
                        param = param + "]"
                    }
                    //if(mapEntry.valueExpression.type.toString() == "java.lang.Object"){
                    else if(mapEntry.valueExpression.getClass().getSimpleName() == "MethodCallExpression"){
                        def size = mapEntry.valueExpression.getArguments().size()
                        for(int l=0 ; l<size; l++){
                            //println(mapEntry.valueExpression.getArguments().getExpression(l).getClass().getSimpleName())
                            if(mapEntry.valueExpression.getArguments().getExpression(l).getClass().getSimpleName() == "MapExpression")
                            {
                                param += processparam(mapEntry.valueExpression,param)
                            }
                            else{
                                param = param + mapEntry.valueExpression.text.toString()
                            }
                        }
                    }
                    //else if(mapEntry.valueExpression.getClass().getSimpleName() == "MapExpression"){
                        //MapExpression key = mapEntry.valueExpression
                        //def list = key.getMapEntryExpressions()
                        //param = param + "\"" + mapEntry.valueExpression.text.toString() +"\""
                    //}
                    else if(mapEntry.valueExpression.type.toString() == "java.lang.String"
                            || mapEntry.valueExpression.type.toString() == "groovy.lang.GString"){
                        param = param + "\"" + mapEntry.valueExpression.text.toString() +"\""
                    }
                    else{
                        param = param + mapEntry.valueExpression.text.toString()
                    }
                    if(k != maplist.size()-1){
                        param = param + ","
                    }
                }
            }
            else{
                param = param + sub.getArguments().getExpression(0).text.toString()
            }
        }
    }
    //else if (str.contains(",")){
    else if (sub.getArguments().size() >= 2){
        //println(sub.getArguments().type) java.lang.Object
        //println(sub.getArguments().getType()) java.lang.Object
        //TupleExpression tuple = sub.getArguments()
        for (int j = 0; j < sub.getArguments().size(); j++) {
            if (sub.getArguments().getExpression(j).type.toString() == "java.lang.String"
                    || sub.getArguments().getExpression(j).type.toString() == "groovy.lang.GString"){
                param = param + "\"" + sub.getArguments().getExpression(j).text.toString() +"\""
                if(j != sub.getArguments().size()-1){
                    param = param + ","
                }
            }
//            else{
//                //MapExpression
//                //NamedArgumentListExpression namedArgumentListExpression = tuple.getExpression(j)
//
//                def maplist = sub.getArguments().getExpression(j).getMapEntryExpressions()
//                for (int k = 0; k < maplist.size(); k++) {
//                    MapEntryExpression mapEntry = maplist[k]
//                    //argmap.put(mapEntry.keyExpression.text, mapEntry.valueExpression.text)
//                    //println(mapEntry.valueExpression.type)
//                    param = param + mapEntry.keyExpression.text.toString() + ":"
//                    //if(mapEntry.valueExpression.type.toString() == "java.lang.Object"){
//                    if(mapEntry.valueExpression.getClass().getSimpleName() == "MethodCallExpression"){
//                        def size = mapEntry.valueExpression.getArguments().size()
//                        for(int l=0 ; l<size; l++){
//                            //println(mapEntry.valueExpression.getArguments().getExpression(l).getClass().getSimpleName())
//                            if(mapEntry.valueExpression.getArguments().getExpression(l).getClass().getSimpleName() == "MapExpression")
//                            {
//                                param += processparam(mapEntry.valueExpression,param)
//                            }
//                            else{
//                                param = param + mapEntry.valueExpression.text.toString()
//                            }
//                        }
//                    }
//                    else if(mapEntry.valueExpression.type.toString() == "java.lang.String"
//                            || mapEntry.valueExpression.type.toString() == "groovy.lang.GString"){
//                        param = param + "\"" + mapEntry.valueExpression.text.toString() +"\""
//                    }else{
//                        param = param + mapEntry.valueExpression.text.toString()
//                    }
//                    if(k != maplist.size()-1){
//                        param = param + ","
//                    }
//                }
//            }
            }
        }
        else{
            param = str.substring(1,str.length()-1)
        }
        return param
    }
     */

}
