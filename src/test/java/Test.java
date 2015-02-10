import java.util.Random;

public class Test {
    static int   result      = 0;                                        //  ���ս��
    static int[] wallHeights = new int[] { 1, 6, 1, 2, 3, 4, 100, 1, 9 }; //  ��ʾ���е�ǽ�ĸ߶�

    public static void main(String[] args) {
        Random r = new Random();
        wallHeights = new int[500000];
        for (int i = 0; i < 500000; i++) {
            wallHeights[i] = r.nextInt(100);
        }
        long start = System.currentTimeMillis();
        process(0, wallHeights.length - 1);
        System.out.println("CALC 1: " + result);
        System.out.println("CALC 1: " + (System.currentTimeMillis() - start));
        start = System.currentTimeMillis();
        resolve();
        System.out.println("CALC 2: " + (System.currentTimeMillis() - start));
        start = System.currentTimeMillis();
        System.out.println("CALC 3: " + calculate(wallHeights));
        System.out.println("CALC 3: " + (System.currentTimeMillis() - start));
    }
    
    public static int calculate(int [] testcase){
        int p_l = 0;
        int p_r = testcase.length - 1;
        int max_l = testcase[p_l];
        int max_r = testcase[p_r];
 
        int volume = 0;
        while (p_r > p_l) {
            if (max_l < max_r){
                p_l++;
                if (testcase[p_l] >= max_l){
                    max_l = testcase[p_l];
                }else{
                    volume += max_l - testcase[p_l];
                }
            }else{
                p_r--;
                if (testcase[p_r] >= max_r){
                    max_r = testcase[p_r];
                }else{
                    volume += max_r - testcase[p_r];
                }
            }
        }
 
        return volume;
    }
    public static void resolve() {
        // �����һ����
        int first,last;
        // �ӵ�1�㿪ʼ
        int cntHeight = 1;
        int allCount = 0;
        do {
            // �����һ����
            first = Integer.MAX_VALUE;
            // �������һ����
            last = Integer.MIN_VALUE;
            for (int i = 0; i < wallHeights.length; i++) {
                if (wallHeights[i] >= cntHeight) {
                    // �ҵ���һ��
                    first = i;
                    break;
                }
            }
            for (int i = wallHeights.length - 1; i >= 0; i--) {
                if (wallHeights[i] >= cntHeight) {
                    // �ҵ����һ��
                    last = i;
                    break;
                }
            }
            if (first < last) {
                for (int i = first; i < last; i++) {
                    if (wallHeights[i] < cntHeight) {
                        // ���Է���ˮ�ĕr��
                        allCount++;
                    }
                }
            } else {
                break;
            }
            cntHeight++;
            // ��first<last��ʱ��˵��������������ǽ����
        } while (true);
        System.out.println("CALC 2: " + allCount);
    }

    public static void process(int start, int end) {
        //  first��start��end֮����ߵ�ǽ
        //  second��start��end֮��ڶ��ߵ�ǽ
        int first = 0, second = 0;
        //  firstIndex����һ�ߵ�ǽ��wallHeights�е�����
        //  secondIndex���ڶ��ߵ�ǽ��wallHeights�е�����
        int firstIndex = 0, secondIndex = 0;
        //  ����ǽ����������һ��ǽ�ľ���
        if (end - start <= 1)
            return;
        //  ��ʼ��ȡ��һ�ߺ͵ڶ���ǽ��ש��
        for (int i = start; i <= end; i++) {
            if (wallHeights[i] > first) {
                second = first;
                secondIndex = firstIndex;
                first = wallHeights[i];
                firstIndex = i;
            } else if (wallHeights[i] > second) {
                second = wallHeights[i];
                secondIndex = i;
            }
        }

        //  ��ȡ���ǽ������
        int startIndex = Math.min(firstIndex, secondIndex);
        //  ��ȡ�Ҳ�ǽ������
        int endIndex = Math.max(firstIndex, secondIndex);
        //  �������
        int distance = endIndex - startIndex;
        //  �����һ�ߵ�ǽ�͵ڶ��ߵ�ǽ֮��������һ��ǽ����ô��ʼ����������ǽ֮����ԷŶ��ٸ���λ��ˮ
        if (distance > 1) {
            result = result + (distance - 1) * second;
            //  ��ȥ������ǽ֮���ש��
            for (int i = startIndex + 1; i < endIndex; i++) {
                result -= wallHeights[i];
            }

        }
        //  ��ʼ�ݹ鴦�����ǽ���뿪ʼλ���ܷŶ���ˮ
        process(start, startIndex);
        //  ��ʼ�ݹ鴦���Ҳ�ǽ�������λ���ܷŶ���ˮ
        process(endIndex, end);
    }

}