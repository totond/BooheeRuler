package yanzhikai.ruler.InnerRulers;

/**
 * author : yany
 * e-mail : yanzhikai_yjk@qq.com
 * time   : 2017/12/13
 * desc   : 引入cache来减少计算量，不过效果不明显，先不使用
 */

public class MathUtils {
    private static float inputCache = 0, outputCache = 0;

    public static float round(float input){
        if (inputCache == input){
            return outputCache;
        }
        inputCache = input;
        outputCache = Math.round(input);
        return outputCache;
    }
}
