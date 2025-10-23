package com.onesockpirates.quad.assignment.trivia.managers;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.io.FileNotFoundException;
import java.util.Scanner;
import com.google.gson.*;

import org.springframework.stereotype.Component;

@Component
public class StorageManager<T> implements IStorageManager<T>{

    private File database;
    private Class<T> type;

    public void intialize(String name, Class<T> type){
        this.type = type;
        try {
            this.database = new File("database" + name + ".txt");
            if (this.database.createNewFile()) {
                System.out.println("Database created");
            } else {
                System.out.println("Database already exists.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public T save(T in) {
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        String jsonStr = gson.toJson(in);
        try (var fw = new FileWriter(this.database.getAbsolutePath(), StandardCharsets.UTF_8, true)) {
            fw.append("\n"+jsonStr);
            return in;
        } catch (Exception e ){
			e.printStackTrace();
            return null;
        }
    }

    public T query(String queryFilter){
        try {
			Scanner scanner = new Scanner(this.database);
            String record = "";
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
                if (line.contains(queryFilter)) {
                    record = line;
                    break;
                }
			}
			scanner.close();
            if (record.isBlank()) return null;
            else return new Gson().fromJson(record, this.type);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
        return null;
    }
    
    public void update(String queryFilter, T updated){
        try {
			Scanner scanner = new Scanner(this.database);
            String newDBContents = "";
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
                if (!line.contains(queryFilter)) {
                    newDBContents += line + "\n";
                }
			}
			scanner.close();
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            String jsonStr = gson.toJson(updated);
            newDBContents +=  jsonStr + "\n";
            try (var fw = new FileWriter(this.database.getAbsolutePath(), StandardCharsets.UTF_8, false)) {
                fw.write(newDBContents.trim());
            } catch (Exception e ){
                e.printStackTrace();
            }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    }
}
