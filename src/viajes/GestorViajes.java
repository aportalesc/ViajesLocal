package viajes;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;

public class GestorViajes {

	private static FileWriter os;			// stream para escribir los datos en el fichero
	private static FileReader is;			// stream para leer los datos del fichero

	/**
	 * 	Diccionario para manejar los datos en memoria.
	 * 	La clave es el codigo único del viaje.
 	 */
	private static HashMap<String, Viaje> mapa;


	/**
	 * Constructor del gestor de viajes
	 * Crea o Lee un fichero con datos de prueba
	 */
	public GestorViajes() {
		mapa =  new HashMap<String, Viaje>();
		File file = new File("viajes.json");
		try {
			if (!file.exists() ) {
				// Si no existe el fichero de datos, los genera y escribe
				os = new FileWriter(file);
				generaDatos();
				escribeFichero(os);
				os.close();
			}
			// Si existe el fichero o lo acaba de crear, lo lee y rellena el diccionario con los datos
			is = new FileReader(file);
			leeFichero(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Cuando cada cliente cierra su sesion volcamos los datos en el fichero para mantenerlos actualizados
	 */
	public void guardaDatos(){
		File file = new File("viajes.json");
		try {
			os = new FileWriter(file);
			escribeFichero(os);
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * escribe en el fichero un array JSON con los datos de los viajes guardados en el diccionario
	 * 
	 * @param os	stream de escritura asociado al fichero de datos
	 */
	private void escribeFichero(FileWriter os) {
		// Pasamos los viajes del mapa a un JSONArray
		JSONArray array = new JSONArray();
		for(Viaje v: mapa.values())
			array.add(v);

		try {
			// Escribe en el fichero los datos de los viajes del JSONArray
			os.write(array.toJSONString());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


	/**
	 * Genera los datos iniciales
	 */
	private void generaDatos() {

		Viaje viaje = new Viaje("pedro", "Castellón", "Alicante", "28-05-2023", 16, 1);
		mapa.put(viaje.getCodviaje(), viaje);

		viaje = new Viaje("pedro", "Alicante", "Castellón", "29-05-2023", 16, 1);
		mapa.put(viaje.getCodviaje(), viaje);

		viaje = new Viaje("maria", "Madrid", "Valencia", "07-06-2023", 7, 2);
		mapa.put(viaje.getCodviaje(), viaje);

		viaje = new Viaje("carmen", "Sevilla", "Barcelona", "12-08-2023", 64, 1);
		mapa.put(viaje.getCodviaje(), viaje);

		viaje = new Viaje("juan", "Castellón", "Cordoba", "07-11-2023", 39, 3);
		mapa.put(viaje.getCodviaje(), viaje);

	}

	/**
	 * Lee los datos del fichero en formato JSON y los añade al diccionario en memoria
	 *
	 * @param is	stream de lectura de los datos del fichero
	 */
	private void leeFichero(FileReader is) {
		JSONParser parser = new JSONParser();
		try {
			// Leemos toda la información del fichero en un array de objetos JSON
			JSONArray array = (JSONArray) parser.parse(is);
			// Rellena los datos del diccionario en memoria a partir del JSONArray
			rellenaDiccionario(array);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Rellena el diccionario a partir de los datos en un JSONArray
	 *
	 * @param array	JSONArray con los datos de los Viajes
	 */
	private void rellenaDiccionario(JSONArray array) {

		for(int i = 0; i < array.size(); i++) {
			Viaje v = new Viaje((JSONObject) array.get(i));
			mapa.put(v.getCodviaje(), v);
		}
	}


	/**
	 * Devuelve los viajes disponibles con un origen dado
	 * 
	 * @param origen
	 * @return JSONArray de viajes con un origen dado. Vacío si no hay viajes disponibles con ese origen
	 */
	public JSONArray consultaViajes(String origen) {

		JSONArray ret = new JSONArray();
		for(Viaje v: mapa.values()){
			if(v.getOrigen().equals(origen))
				ret.add(v);
		}
		return ret;
	}


	/**
	 * El cliente codcli reserva el viaje codviaje
	 * 
	 * @param codviaje
	 * @param codcli
	 * @return JSONObject con la información del viaje. Vacío si no existe o no está disponible
	 */
	public JSONObject reservaViaje(String codviaje, String codcli) {

		JSONObject reserva = new JSONObject();

		if(!mapa.containsKey(codviaje))
			return reserva;

		Viaje viaje = mapa.get(codviaje);

		if (!viaje.finalizado() && !viaje.getCodprop().equals(codcli)) {
			if (viaje.anyadePasajero(codcli))
				reserva = viaje.toJSON();
		}

		return reserva;
	}

	/**
	 * El cliente codcli anula su reserva del viaje codviaje
	 * 
	 * @param codviaje	codigo del viaje a anular
	 * @param codcli	codigo del cliente
	 * @return	JSON del viaje en que se ha anulado la reserva. JSON vacio si no se ha anulado
	 */
	public JSONObject anulaReserva(String codviaje, String codcli) {

		JSONObject anulado = new JSONObject();

		if(!mapa.containsKey(codviaje))
			return anulado;

		Viaje viaje = mapa.get(codviaje);

		if(!viaje.finalizado()) {
			if(viaje.borraPasajero(codcli))
				anulado = viaje.toJSON();
		}
		return anulado;
	}

	/**
	 * Devuelve si una fecha es válida y futura
	 * @param fecha
	 * @return
	 */
	private boolean es_fecha_valida(String fecha) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

		try {
			LocalDate dia = LocalDate.parse(fecha, formatter);
			LocalDate hoy = LocalDate.now();

			return dia.isAfter(hoy);
		}
		catch (DateTimeParseException e) {
			System.out.println("Fecha invalida: " + fecha);
			return false;
		}

	}

	/**
	 * El cliente codcli oferta un Viaje
	 * @param codcli
	 * @param origen
	 * @param destino
	 * @param fecha
	 * @param precio
	 * @param numplazas
	 * @return	JSONObject con los datos del viaje ofertado
	 */
	public JSONObject ofertaViaje(String codcli, String origen, String destino, String fecha, long precio, long numplazas) {

		Viaje viaje = new Viaje(codcli, origen, destino, fecha, precio, numplazas);
		JSONArray array = new JSONArray();
		array.add(viaje.toJSON());

		rellenaDiccionario(array);

		return viaje.toJSON();
	}



	/**
	 * El cliente codcli borra un viaje que ha ofertado
	 * 
	 * @param codviaje	codigo del viaje a borrar
	 * @param codcli	codigo del cliente
	 * @return	JSONObject del viaje borrado. JSON vacio si no se ha borrado
	 */
	public JSONObject borraViaje(String codviaje, String codcli) {

		JSONObject borrado = new JSONObject();
		if(!mapa.containsKey(codviaje))
			return borrado;

		Viaje v = mapa.get(codviaje);

		if (v.getCodprop().equals(codcli) && !v.finalizado()){
			borrado = v.toJSON();
			mapa.remove(codviaje);
		}
		return borrado;
	}


}
