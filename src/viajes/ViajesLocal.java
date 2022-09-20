package viajes;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Scanner;


public class ViajesLocal {


	/**
	 * Muestra el menu de opciones y lee repetidamente de teclado hasta obtener una opcion valida
	 * @param teclado	stream para leer la opción elegida de teclado
	 * @return			opción elegida
	 */
	public static int menu(Scanner teclado) {
		int opcion;
		System.out.println("\n\n");
		System.out.println("=====================================================");
		System.out.println("============            MENU        =================");
		System.out.println("=====================================================");
		System.out.println("0. Salir");
		System.out.println("1. Consultar viajes con un origen dado");
		System.out.println("2. Reservar un viaje");
		System.out.println("3. Anular una reserva");
		System.out.println("4. Ofertar un viaje");
		System.out.println("5. Borrar un viaje");
		do {
			System.out.print("\nElige una opcion (0..5): ");
			opcion = teclado.nextInt();
		} while ( (opcion<0) || (opcion>5) );
		teclado.nextLine(); // Elimina retorno de carro del buffer de entrada
		return opcion;
	}


	/**
	 * Programa principal. Muestra el menú repetidamente y atiende las peticiones del cliente.
	 * 
	 * @param args	no se usan argumentos de entrada al programa principal
	 */
	public static void main(String[] args)  {

		Scanner teclado = new Scanner(System.in);

		// Crea un gestor de viajes
		GestorViajes gestor = new GestorViajes();

		System.out.print("Introduce tu codigo de cliente: ");
		String codcli = teclado.nextLine();

		int opcion; 
		do {
			opcion = menu(teclado);
			switch (opcion) {
			case 0: // Guardar los datos en el fichero y salir del programa

				gestor.guardaDatos();
				System.out.println("Los datos se han guardado correctamente");

				break;

			case 1: { // Consultar viajes con un origen dado

				// POR IMPLEMENTAR
				System.out.print("Escriba el origen del viaje: ");
				String org = teclado.nextLine();
				JSONArray viajes = gestor.consultaViajes(org);
				if(viajes.isEmpty())
					System.out.println("\nLo sentimos, no hay ningún viaje con el origen especificado");
				else
					System.out.println(viajes.toJSONString());

				break;
			}

			case 2: { // Reservar un viaje

				System.out.print("Introduce el código del viaje a reservar: ");
				String codviaje = teclado.nextLine();
				JSONObject reserva = gestor.reservaViaje(codviaje, codcli);
				if(reserva.isEmpty())
					System.out.println("Lo sentimos, no se puede reservar este viaje");
				else
					System.out.println("El viaje ha sido reservado correctamente");

				break;
			}

			case 3: { // Anular una reserva

				System.out.print("Introduce el código del viaje a anular: ");
				String codviaje = teclado.nextLine();
				JSONObject anulado = gestor.anulaReserva(codviaje, codcli);
				if(anulado.isEmpty())
					System.out.println("Lo sentimos, no se ha podido anular la reserva");
				else
					System.out.println("La reserva ha sido anulada correctamente");

				break;
			}

			case 4: { // Ofertar un viaje
				System.out.print("Introduce el origen del viaje: ");
				String origen = teclado.nextLine();
				System.out.print("Introduce el destino del viaje: ");
				String destino = teclado.nextLine();
				System.out.print("Introduce la fecha del viaje: ");
				String fecha = teclado.nextLine();
				System.out.print("Introduce el precio de viaje: ");
				long precio = teclado.nextLong();
				System.out.print("Introduce el número de plazas del viaje: ");
				long numplazas = teclado.nextLong();
				JSONObject json = gestor.ofertaViaje(codcli, origen, destino, fecha, precio, numplazas);

				break;
			}

			case 5: { // Borrar un viaje ofertado
				System.out.print("Introduce el código del viaje: ");
				String codviaje = teclado.nextLine();
				JSONObject borrado = gestor.borraViaje(codviaje, codcli);

				if(borrado.isEmpty())
					System.out.println("Lo sentimos, no se ha podido borrar el viaje.");
				else
					System.out.println("El viaje se ha borrado correctamente");


				break;
			}

			} // fin switch

		} while (opcion != 0);

	} // fin de main

} // fin class
