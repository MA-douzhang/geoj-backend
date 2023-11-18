package com.madou.geojbackendquestionservice;

/**
 * @author MA_dou
 * @version 1.0
 * @project geoj-backend-microservice
 * @description
 * @date 2023/11/18 12:29:58
 */
public class aitest {
    public static void main(String[] args) {
        String test = "\n" +
                "题目解题思路。\n" +
                "题目要求计算A+B的值，输入为两个整数A和B，输出为A和B的和。\n" +
                "\n" +
                "解题思路为：\n" +
                "1. 首先，定义三个变量a、b和sum，分别用于存储输入的两个整数和它们的和。\n" +
                "2. 使用cin语句从标准输入读取两个整数a和b。\n" +
                "3. 将a和b相加，将结果存储在sum变量中。\n" +
                "4. 输出sum的值到标准输出。\n" +
                "\n" +
                "代码犯错原因：\n" +
                "在求和的过程中，将sum的计算表达式修改为了a + b + 1，导致结果不正确。\n" +
                "\n" +
                "正确的解题代码：\n" +
                "#include <iostream>\n" +
                "using namespace std;\n" +
                "int main() {\n" +
                "    int a, b, sum;\n" +
                "    cin >> a >> b;\n" +
                "    sum = a + b; // 正确求和的计算表达式\n" +
                "    cout << sum << endl;\n" +
                "    return 0;\n" +
                "}";
        int solutionIdeaIndex = test.indexOf("题目解题思路");
        int reasonIndex = test.indexOf("代码犯错原因");
        int codeAiIndex = test.indexOf("正确的解题代码");
        String solutionIdea = test.substring(solutionIdeaIndex, reasonIndex);
        String reason = test.substring(reasonIndex, codeAiIndex);
        String codeAi = test.substring(codeAiIndex);
        System.out.println(solutionIdea);
        System.out.println("====================");
        System.out.println(reason);
        System.out.println("====================");
        System.out.println(codeAi);
    }
}
