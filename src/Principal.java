import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.util.Map;
import java.util.Scanner;
import java.util.HashMap;

public class Principal {
    public static void main(String[] args) {
        // Tu API Key personalizada y URL
        String apiKey = "e67046ac4f323ae3c096206a";
        String apiUrl = "https://v6.exchangerate-api.com/v6/" + apiKey + "/latest/USD";

        // Las monedas permitidas para la conversión y sus países
        HashMap<String, String> currencyCountryMap = new HashMap<>();
        currencyCountryMap.put("ARS", "Argentina (Peso argentino)");
        currencyCountryMap.put("BOB", "Bolivia (Boliviano boliviano)");
        currencyCountryMap.put("COP", "Colombia (Peso colombiano)");
        currencyCountryMap.put("VES", "Venezuela (Bolívar soberano)");
        currencyCountryMap.put("CLP", "Chile (Peso chileno)");
        currencyCountryMap.put("USD", "Estados Unidos (Dólar estadounidense)");
        currencyCountryMap.put("PEN", "Perú (Sol peruano)");
        currencyCountryMap.put("BRL", "Brasil (Real brasileño)");

        try {
            // Configurar cliente HTTP
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .GET()
                    .build();

            // Realizar la solicitud y obtener respuesta
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Analizar el JSON
            JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonObject conversionRates = jsonObject.getAsJsonObject("conversion_rates");

            // Filtrar las tasas de cambio para las monedas permitidas
            JsonObject filteredRates = new JsonObject();
            for (String currency : currencyCountryMap.keySet()) {
                if (conversionRates.has(currency)) {
                    filteredRates.add(currency, conversionRates.get(currency));
                }
            }

            // Convertir a un Map para un acceso más fácil
            Map<String, Double> rates = new com.google.gson.Gson().fromJson(filteredRates, Map.class);

            // Interfaz de usuario
            Scanner scanner = new Scanner(System.in);
            System.out.println("Bienvenido al Conversor de Monedas");
            System.out.println("Monedas disponibles:");
            for (String currency : currencyCountryMap.keySet()) {
                System.out.println(currency + " - " + currencyCountryMap.get(currency));
            }

            while (true) {
                System.out.print("\nIngrese la moneda de origen (ejemplo: USD): ");
                String sourceCurrency = scanner.nextLine().toUpperCase();

                System.out.print("Ingrese la moneda de destino (ejemplo: PEN): ");
                String targetCurrency = scanner.nextLine().toUpperCase();

                System.out.print("Ingrese el monto a convertir: ");
                double amount = scanner.nextDouble();
                scanner.nextLine(); // Consumir nueva línea

                // Validar si las monedas están disponibles
                if (rates.containsKey(sourceCurrency) && rates.containsKey(targetCurrency)) {
                    double sourceRate = rates.get(sourceCurrency);
                    double targetRate = rates.get(targetCurrency);

                    // Calcular conversión
                    double convertedAmount = (amount / sourceRate) * targetRate;
                    System.out.printf("Resultado: %.2f %s son %.2f %s\n",
                            amount, sourceCurrency, convertedAmount, targetCurrency);
                } else {
                    System.out.println("Moneda no válida. Intente nuevamente.");
                }

                // Preguntar si desea realizar otra conversión
                System.out.print("\n¿Desea realizar otra conversión? (sí/no): ");
                String continueOption = scanner.nextLine().toLowerCase();

                // Cambiar la validación para aceptar variantes de "sí" y "no" (por ejemplo, "si", "sí", "no", "noo", etc.)
                if (continueOption.equals("no") || continueOption.equals("noo")) {
                    System.out.println("¡Gracias por usar el Conversor de Monedas!");
                    break; // Si el usuario dice "no" o alguna variante, se sale del bucle
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
