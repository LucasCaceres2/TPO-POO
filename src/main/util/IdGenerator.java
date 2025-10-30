package main.util;

public final class IdGenerator {
    private IdGenerator() {}

    // Sufijo ultra simple: <millis>-<0000..9999>
    private static String tsRand() {
        long now = System.currentTimeMillis();
        int r = (int) (Math.random() * 10_000); // 0..9999
        return now + "-" + String.format("%04d", r);
    }

    public static String newUsuarioId() {
        return "USR-" + tsRand();
    }

    public static String newLegajo() {
        return "LEG-" + tsRand();
    }

    public static String newMatricula() {
        return "MAT-" + tsRand();
    }

    public static String newCursoId() {
        return "CUR-" + tsRand();
    }
    public static String newAreaId() {
        return "AR-"  + tsRand();
    }
    public static String newPagoId() {
        return "PAG-" + tsRand();
    }
    public static String newInscripcionId() {
        return "INS-" +  tsRand();
    }
}