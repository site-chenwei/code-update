package site.chenwei.update.common.serialNo;

import site.chenwei.update.common.exception.SerialException;
import site.chenwei.update.common.model.SerialNo;
import site.chenwei.update.mapper.SerialNoMapper;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author cw
 * @date 2022年03月16日 20:53
 */
public class SerialNoGenerator {
    private final SerialNoMapper serialNoMapper;
    private SerialNo serialNo;

    public SerialNoGenerator(SerialNoMapper serialNoMapper, SerialNo serialNo) {
        this.serialNoMapper = serialNoMapper;
        this.serialNo = serialNo;
    }

    public synchronized String generate() throws SerialException {
        SerialNo serialNo = serialNoMapper.selectByPrimaryKey(this.serialNo.getId());
        serialNo.setValue(serialNo.getValue() + 1);
        String serialStr = format(serialNo.getValue(), serialNo.getFormat());
        serialNoMapper.updateByPrimaryKey(serialNo);
        this.serialNo = serialNo;
        return serialStr;
    }

    private String format(Long value, String format) throws SerialException {
        try {
            String dateFormat = format.replaceAll("#", "");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
            String formattedDateStr = simpleDateFormat.format(new Date());
            StringBuilder stringBuilder = new StringBuilder(format.replace(dateFormat, formattedDateStr));
            String valueStr = String.valueOf(value);
            int zeroIndex = formattedDateStr.length();
            while (zeroIndex < format.length() - valueStr.length()) {
                stringBuilder.replace(zeroIndex, zeroIndex + 1, "0");
                zeroIndex++;
            }
            int valueIndex = zeroIndex;
            while (zeroIndex < format.length()) {
                stringBuilder.replace(zeroIndex, zeroIndex + 1, String.valueOf(valueStr.charAt(zeroIndex - valueIndex)));
                zeroIndex++;
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            throw new SerialException("生成序列号失败");
        }
    }


    public static void main(String[] args) throws SerialException {
        SerialNoGenerator serialNoGenerator = new SerialNoGenerator(null, null);
        String format = serialNoGenerator.format(0L, "yyyyMMdd########");
        System.out.println();
    }
}
