package com.magazin;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class DataService {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void salveazaProduse(List<Produs> produse) throws IOException {
        mapper.writeValue(new File("produse.json"), produse);
    }

    // Exemplu cum încarci datele la pornirea aplicației
    public static List<Produs> incarcaProduse() throws IOException {
        File file = new File("produse.json");
        if (!file.exists()) return null;
        return mapper.readValue(file, mapper.getTypeFactory().constructCollectionType(List.class, Produs.class));
    }
}