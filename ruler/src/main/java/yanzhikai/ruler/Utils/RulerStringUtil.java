package yanzhikai.ruler.Utils;


/**
 * author : yany
 * e-mail : yanzhikai_yjk@qq.com
 * time   : 2017/12/25
 * desc   : 处理刻度值字符串
 */

public class RulerStringUtil {
    private static float mFactorCache = 0;
    private static float mDividerCache = 1;

    /**
     * 用于计算刻度值实际显示数值并转化为String，
     * 使用分条件处理是因为浮点运算不是很准确，所以显示float值的时候使用除法而不用乘法，避免了72.0显示成72.00005的情况。
     * @param input 输入值
     * @param factor 乘积因子，限制为正数
     * @return 返回结果字符串
     */
    public static String resultValueOf(float input, float factor){
        if (factor >= 1){
            return String.valueOf((int)(input * factor));
        }else if (factor > 0){
            if (mFactorCache != factor) {
                mFactorCache = factor;
                mDividerCache = 1 / factor;
            }
            return String.valueOf(input / mDividerCache);
        }else {
            try {
                throw new Exception("Invalid factor!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }
}
