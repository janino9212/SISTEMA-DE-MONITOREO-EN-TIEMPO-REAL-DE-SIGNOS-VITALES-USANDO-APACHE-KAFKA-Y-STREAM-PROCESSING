package co.edu.uts.vitales.simulador;

import co.edu.uts.vitales.common.LecturaVital;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Genera lecturas con ruido alrededor de un perfil estable por paciente (RF-01, RF-07).
 * Con la misma semilla produce la misma secuencia de valores.
 * TODO RF-03/RF-04: anomalías configurables (transitoria, sostenida, combinada) y bitácora de inyecciones.
 */
final class GeneradorLecturas {

    private record Perfil(String id, int fcBase, int pasBase, int padBase, double tempBase, int spo2Base) {
    }

    private final Random azar;
    private final String idEscenario;
    private final List<Perfil> perfiles = new ArrayList<>();

    GeneradorLecturas(int pacientes, long semilla, String idEscenario) {
        this.azar = new Random(semilla);
        this.idEscenario = idEscenario;
        for (int i = 1; i <= pacientes; i++) {
            perfiles.add(new Perfil(
                    String.format("P-%04d", i),
                    60 + azar.nextInt(26),
                    115 + azar.nextInt(21),
                    70 + azar.nextInt(16),
                    36.5 + azar.nextInt(6) / 10.0,
                    97 + azar.nextInt(3)));
        }
    }

    /** Una lectura por paciente. Si crisisDemo es true, el primer paciente entra en hipoxemia con taquicardia. */
    List<LecturaVital> siguienteLote(long ahoraMs, boolean crisisDemo) {
        List<LecturaVital> lote = new ArrayList<>(perfiles.size());
        for (int i = 0; i < perfiles.size(); i++) {
            Perfil p = perfiles.get(i);
            int fc = p.fcBase() + azar.nextInt(7) - 3;
            int pas = p.pasBase() + azar.nextInt(9) - 4;
            int pad = p.padBase() + azar.nextInt(7) - 3;
            double temp = redondear(p.tempBase() + azar.nextGaussian() * 0.05);
            int spo2 = Math.min(100, p.spo2Base() + azar.nextInt(3) - 1);
            if (crisisDemo && i == 0) {
                fc = 135;
                spo2 = 88;
            }
            lote.add(new LecturaVital(p.id(), ahoraMs, fc, pas, pad, temp, spo2, idEscenario));
        }
        return lote;
    }

    private static double redondear(double valor) {
        return Math.round(valor * 10.0) / 10.0;
    }
}
