package gm.tools.editor.gui;

import gm.tools.editor.character.skill.Skill;
import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.converter.IntegerStringConverter;
import lombok.Getter;

import java.util.Collection;

public class TraitTable {
	@Getter
	private TableView<TraitTableEntry> traitTable = new TableView<>();

	public TraitTable() {
		// 1.column

		TableColumn traitCol = new TableColumn("Trait");
		traitCol.setMinWidth(200);
		traitCol.setCellValueFactory(new PropertyValueFactory<TraitTableEntry, String>("name"));
		traitCol.setCellFactory(TextFieldTableCell.forTableColumn());
		traitCol.setOnEditCommit(
				new EventHandler<TableColumn.CellEditEvent<TraitTableEntry, String>>() {
					@Override
					public void handle(TableColumn.CellEditEvent<TraitTableEntry, String> t) {
						TraitTableEntry entry = t.getTableView().getItems().get(t.getTablePosition().getRow());
						entry.setName(t.getNewValue());
					}
				}
		);

		//  2.column

		TableColumn costCol = new TableColumn("Cost");
		costCol.setMinWidth(100);
		costCol.setCellValueFactory(new PropertyValueFactory<TraitTableEntry, Integer>("cost"));
		costCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		costCol.setOnEditCommit(
				new EventHandler<TableColumn.CellEditEvent<TraitTableEntry, Integer>>() {
					@Override
					public void handle(TableColumn.CellEditEvent<TraitTableEntry, Integer> t) {
						TraitTableEntry entry = t.getTableView().getItems().get(t.getTablePosition().getRow());
						entry.setCost(t.getNewValue());
					}
				}
		);

		// table

		traitTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		traitTable.setEditable(true);
		traitTable.getColumns().addAll(traitCol, costCol);
		addRow();

		// handle enter key

		traitTable.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
			if (event.getCode() == KeyCode.ENTER) {
				handleEnter();
			} else if (event.getCode() == KeyCode.DELETE) {
				handleDelete();
			}
		});
	}

	private void handleEnter() {
		TablePosition pos = traitTable.getFocusModel().getFocusedCell();

		if (pos.getRow() == traitTable.getItems().size() - 1) {
			addRow();
		} else if (pos.getRow() >= 0) {
			traitTable.getSelectionModel().clearAndSelect(pos.getRow() + 1, pos.getTableColumn());
		}
	}

	private void handleDelete() {
		int selectedIndex = traitTable.getSelectionModel().getSelectedIndex();

		if (selectedIndex >= 0) {
			traitTable.getItems().remove(selectedIndex);
		}
	}

	private void addRow() {
		TablePosition pos = traitTable.getFocusModel().getFocusedCell();

		traitTable.getSelectionModel().clearSelection();

		// create new record and add it to the model
		TraitTableEntry entry = new TraitTableEntry("?", 0);
		traitTable.getItems().add(entry);

		// get last row
		int row = traitTable.getItems().size() - 1;
		traitTable.getSelectionModel().select(row, pos.getTableColumn());

		// scroll to new row
		traitTable.scrollTo(entry);
	}

	public Collection<TraitTableEntry> getTraitTableEntries() {
		return traitTable.getItems();
	}

	public void clear() {
		traitTable.getItems().clear();
	}

	public void add(String name, int cost) {
		TraitTableEntry entry = new TraitTableEntry(name, cost);
		traitTable.getItems().add(entry);
	}
}
