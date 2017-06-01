package miniceduapp.helpers

import javafx.scene.control.TableRow
import javafx.scene.control.TableView

fun <T> TableView<T>.setOnRowDoubleClick(op: (T) -> Unit) {
    setRowFactory {
        val row = TableRow<T>()
        row.setOnMouseClicked {
            if (it.clickCount == 2 && !row.isEmpty) {
                op(row.item)
            }
        }
        row
    }
}
