package ui.util;

import javax.swing.JTable;
import javax.swing.table.TableModel;
import java.io.FileWriter;
import java.io.IOException;

public class TableExportUtil {

    public static boolean exportToCsv(JTable table, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {

            TableModel model = table.getModel();

            // Write header
            for (int col = 0; col < model.getColumnCount(); col++) {
                writer.write(model.getColumnName(col));
                if (col < model.getColumnCount() - 1) writer.write(",");
            }
            writer.write("\n");

            // Write rows
            for (int row = 0; row < model.getRowCount(); row++) {
                for (int col = 0; col < model.getColumnCount(); col++) {
                    Object value = model.getValueAt(row, col);
                    writer.write(value != null ? value.toString() : "");
                    if (col < model.getColumnCount() - 1) writer.write(",");
                }
                writer.write("\n");
            }

            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
