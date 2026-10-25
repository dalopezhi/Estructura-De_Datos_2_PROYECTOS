package compression;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Huffman {

    private String[] codigos = new String[65536];

    private void generarCodigos(NodoHuffman nodo, String codigo) {

        if (nodo == null) {
            return;
        }

        if (nodo.esHoja()) {
            codigos[nodo.caracter] = codigo.isEmpty() ? "0" : codigo;
            return;
        }

        generarCodigos(nodo.izquierda, codigo + "0");
        generarCodigos(nodo.derecha, codigo + "1");
    }

    public void comprimir(String texto, String rutaDestino)
            throws IOException {

        // Contar las frecuencias de los caracteres
        int[] frecuencias = new int[65536];

        for (int i = 0; i < texto.length(); i++) {
            frecuencias[texto.charAt(i)]++;
        }

        ColaPrioridad cola = new ColaPrioridad();

        for (int i = 0; i < frecuencias.length; i++) {
            if (frecuencias[i] > 0) {
                cola.insertar(new NodoHuffman((char) i, frecuencias[i]));
            }
        }

        // Construir el arbol Huffman
        while (cola.getCantidad() > 1) {

            NodoHuffman izquierdo = cola.extraerMenor();
            NodoHuffman derecho = cola.extraerMenor();

            NodoHuffman padre = new NodoHuffman(
                    '\0', izquierdo.frecuencia + derecho.frecuencia);

            padre.izquierda = izquierdo;
            padre.derecha = derecho;

            cola.insertar(padre);
        }

        codigos = new String[65536];

        if (cola.getCantidad() > 0) {
            generarCodigos(cola.extraerMenor(), "");
        }

        // Convertir el texto a bits Huffman
        StringBuilder bits = new StringBuilder();

        for (int i = 0; i < texto.length(); i++) {
            bits.append(codigos[texto.charAt(i)]);
        }

        int cantidadBits = bits.length();

        // Agrupar los bits en bytes
        ByteArrayOutputStream datos = new ByteArrayOutputStream();

        for (int i = 0; i < cantidadBits; i += 8) {

            int valor = 0;

            for (int j = 0; j < 8; j++) {
                valor <<= 1;

                if (i + j < cantidadBits &&
                        bits.charAt(i + j) == '1') {
                    valor |= 1;
                }
            }

            datos.write(valor);
        }

        String contenidoBase64 = Base64.getEncoder()
                .encodeToString(datos.toByteArray());

        // Guardar tabla de codigos y contenido comprimido
        try (PrintWriter archivo = new PrintWriter(
                new OutputStreamWriter(
                        new FileOutputStream(rutaDestino),
                        StandardCharsets.UTF_8))) {

            int cantidadCaracteres = 0;

            for (String codigo : codigos) {
                if (codigo != null) {
                    cantidadCaracteres++;
                }
            }

            archivo.println(cantidadCaracteres);

            for (int i = 0; i < codigos.length; i++) {
                if (codigos[i] != null) {
                    archivo.println(i + "|" + codigos[i]);
                }
            }

            archivo.println(cantidadBits);
            archivo.println(contenidoBase64);

            if (archivo.checkError()) {
                throw new IOException("Error al guardar el archivo.");
            }
        }
    }

    public String descomprimir(String rutaArchivo)
            throws IOException {

        try (BufferedReader archivo = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(rutaArchivo),
                        StandardCharsets.UTF_8))) {

            int cantidadCaracteres = Integer.parseInt(
                    archivo.readLine());

            if (cantidadCaracteres < 0 || cantidadCaracteres > 65536) {
                throw new IOException("Tabla Huffman invalida.");
            }

            NodoHuffman raiz = new NodoHuffman('\0', 0);

            // Reconstruir el arbol con los codigos guardados
            for (int i = 0; i < cantidadCaracteres; i++) {

                String[] datos = archivo.readLine().split("\\|", -1);

                char caracter = (char) Integer.parseInt(datos[0]);
                String codigo = datos[1];

                NodoHuffman actual = raiz;

                for (int j = 0; j < codigo.length(); j++) {

                    boolean ultimo = j == codigo.length() - 1;
                    char bit = codigo.charAt(j);

                    if (bit == '0') {
                        if (actual.izquierda == null) {
                            actual.izquierda = new NodoHuffman('\0', 0);
                        }
                        actual = actual.izquierda;

                    } else if (bit == '1') {
                        if (actual.derecha == null) {
                            actual.derecha = new NodoHuffman('\0', 0);
                        }
                        actual = actual.derecha;

                    } else {
                        throw new IOException("Codigo Huffman invalido.");
                    }

                    if (ultimo) {
                        actual.caracter = caracter;
                    }
                }
            }

            int cantidadBits = Integer.parseInt(archivo.readLine());

            byte[] contenido = Base64.getDecoder().decode(
                    archivo.readLine());

            if (cantidadBits < 0 || cantidadBits > contenido.length * 8) {
                throw new IOException("Cantidad de bits invalida.");
            }

            StringBuilder resultado = new StringBuilder();
            NodoHuffman actual = raiz;

            for (int i = 0; i < cantidadBits; i++) {

                int valor = contenido[i / 8] & 0xff;
                int bit = (valor >> (7 - i % 8)) & 1;

                actual = bit == 0 ? actual.izquierda : actual.derecha;

                if (actual == null) {
                    throw new IOException("Archivo Huffman dañado.");
                }

                if (actual.esHoja()) {
                    resultado.append(actual.caracter);
                    actual = raiz;
                }
            }

            if (actual != raiz) {
                throw new IOException("Archivo Huffman incompleto.");
            }

            return resultado.toString();

        } catch (RuntimeException error) {
            throw new IOException("Formato Huffman invalido.", error);
        }
    }
}

