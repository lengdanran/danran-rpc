package danran.rpc.common.protocol;

/**
 * @Classname Status
 * @Description TODO
 * @Date 2021/8/23 17:05
 * @Created by ASUS
 */
public enum Status {
    SUCCESS(200, "success"),
    ERROR(500, "error"),
    NOT_FOUND(404, "not found");
    private final int code;
    private final String msg;

    Status(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }
}

