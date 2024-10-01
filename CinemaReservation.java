import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CinemaReservation {

    private static final int ROWS = 15;
    private static final int SEATS_PER_ROW = 46;
    private static final int TOTAL_SEATS = ROWS * SEATS_PER_ROW;

    private final boolean[][] cinemaSeats = new boolean[ROWS][SEATS_PER_ROW];
    private int availableSeats = TOTAL_SEATS;

    public CinemaReservation() {
        for (boolean[] row : cinemaSeats) {
            Arrays.fill(row, false);
        }
    }

    // Sincronizzato per garantire l'accesso univoco ai posti
    public synchronized boolean reserveSeat() {
        // Cerca di trovare i posti centrali non prenotati
        for (int row = 0; row < ROWS; row++) {
            int middle = SEATS_PER_ROW / 2; // Calcolo del posto centrale
            for (int offset = 0; offset <= middle; offset++) {
                int leftSeat = middle - offset;			//quanti posti ho prenotato finora
                int rightSeat = middle + offset;

                // Controlla il posto sinistro
                if (leftSeat >= 0 && !cinemaSeats[row][leftSeat]) {
                    cinemaSeats[row][leftSeat] = true;
                    availableSeats--;
                    System.out.println(Thread.currentThread().getName() + " ha prenotato il posto: Fila " + (row + 1) + ", Posto " + (leftSeat + 1));
                    return true;
                }

                // Controlla il posto destro
                if (rightSeat < SEATS_PER_ROW && !cinemaSeats[row][rightSeat]) {
                    cinemaSeats[row][rightSeat] = true;
                    availableSeats--;
                    System.out.println(Thread.currentThread().getName() + " ha prenotato il posto: Fila " + (row + 1) + ", Posto " + (rightSeat + 1));
                    return true;
                }
            }
        }
        return false; // Nessun posto disponibile
    }

    public synchronized int getAvailableSeats() {
        return availableSeats;
    }

    public static void main(String[] args) throws InterruptedException {
        CinemaReservation cinema = new CinemaReservation();

        // Creazione del pool di thread con 7 spettatori (thread)
        ExecutorService executor = Executors.newFixedThreadPool(7);

        // Creazione e avvio dei thread (spettatori)
        for (int i = 0; i < 7; i++) {
            executor.submit(() -> {
                while (cinema.getAvailableSeats() > 0) {
                    boolean seatReserved = cinema.reserveSeat();
                    if (!seatReserved) {
                        break; // Se non ci sono più posti disponibili, termina
                    }
                    try {
                        // Simula il tempo tra una prenotazione e l'altra
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
        }

        // Lo spettacolo inizia dopo 10 secondi
        Thread.sleep(10000);

        // Ferma l'esecuzione dei thread
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        // Stampa lo stato finale del cinema
        System.out.println("Posti disponibili rimanenti: " + cinema.getAvailableSeats());
        System.out.println("TUTTI I THREAD HANNO COMPLETATO LA PRENOTAZIONE, LO SPETTACOLO PUÒ INIZIARE!");
    }
}
