import com.yupi.yucongming.dev.client.YuCongMingClient;
import com.yupi.yucongming.dev.common.BaseResponse;
import com.yupi.yucongming.dev.model.DevChatRequest;
import com.yupi.yucongming.dev.model.DevChatResponse;

/**
 * @author MA_dou
 * @version 1.0
 * @project geoj-backend-microservice
 * @description
 * @date 2023/11/15 20:27:57
 */
public class test {
    public static void main(String[] args) {

        String accessKey = "ux7bpq5mqr8db3n0dfhd46bunkebr8f3";
        String secretKey = "eg6zsakkz0av0f6jae3g3cw163nw56hc";
        YuCongMingClient client = new YuCongMingClient(accessKey, secretKey);
        // 构造请求
        DevChatRequest devChatRequest = new DevChatRequest();
        devChatRequest.setModelId(1666258198769700865L);
        String request = "分析算法题目内容：\n" +
                "{计算A+B的值\n" +
                "输入\n" +
                "1 1\n" +
                "\n" +
                "输出\n" +
                "2\n" +
                "\n" +
                "要求在时间范围内完成该题目}\n" +
                "我的解题代码：\n" +
                "{import java.util.Scanner;\n" +
                "public class test{\n" +
                "public static void main(String[] args){\n" +
                "Scanner sc = new Scanner(System.in);\n" +
                "int a= sc.nextInt();\n" +
                "int b = sc.nextInt();\n" +
                "System.out.println(a+b+1);\n" +
                "}\n" +
                "}\n" +
                "}";
        devChatRequest.setMessage(request);

// 获取响应
        BaseResponse<DevChatResponse> response = client.doChat(devChatRequest);
        System.out.println(response.getData());


        String responseStr = "【【【【【【\n" +
                "代码中没有按照题目要求直接输出 A + B 的值。\n" +
                "【【【【【【\n" +
                "代码应该改为 `System.out.println(a+b);`\n" +
                "【【【【【【\n" +
                "public class test {\n" +
                "    public static void main(String[] args) {\n" +
                "        Scanner sc = new Scanner(System.in);\n" +
                "        int a = sc.nextInt();\n" +
                "        int b = sc.nextInt();\n" +
                "        System.out.println(a + b);\n" +
                "    }\n" +
                "}\n";
    }
}

