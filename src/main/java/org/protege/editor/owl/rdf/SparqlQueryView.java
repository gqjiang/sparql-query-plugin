package org.protege.editor.owl.rdf;

import org.protege.editor.core.ui.error.ErrorLogPanel;
import org.protege.editor.owl.rdf.repository.BasicSparqlReasonerFactory;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.protege.editor.owl.ui.table.BasicOWLTable;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;

import javax.swing.*;
import java.awt.*;


import javax.swing.filechooser.FileSystemView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class SparqlQueryView extends AbstractOWLViewComponent {

	private SparqlReasoner reasoner;

	private final JTextPane queryPane = new JTextPane();

	private final SwingResultModel resultModel = new SwingResultModel();

	@Override
	protected void initialiseOWLView() {
		initializeReasoner();
		setLayout(new BorderLayout());
		add(createCenterComponent(), BorderLayout.CENTER);
		add(createBottomComponent(), BorderLayout.SOUTH);
		//add(createBottomResultComponent(), BorderLayout.SOUTH);
	}
	
	private void initializeReasoner() {
		try {
			List<SparqlInferenceFactory> plugins = Collections.singletonList((SparqlInferenceFactory) new BasicSparqlReasonerFactory());
			reasoner = plugins.iterator().next().createReasoner(getOWLModelManager().getOWLOntologyManager());
			reasoner.precalculate();
		}
		catch (SparqlReasonerException e) {
			ErrorLogPanel.showErrorDialog(e);
		}
	}
	
	private JComponent createCenterComponent() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0,1));
		queryPane.setText(reasoner.getSampleQuery());
		panel.add(new JScrollPane(queryPane, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER));
		BasicOWLTable results = new BasicOWLTable(resultModel) {
			@Override
			protected boolean isHeaderVisible() {
				return true;
			}
		};
		OWLCellRenderer renderer = new OWLCellRenderer(getOWLEditorKit());
		renderer.setWrap(false);
		results.setDefaultRenderer(Object.class, renderer);
		JScrollPane scrollableResults = new JScrollPane(results);
		panel.add(scrollableResults);
		return panel;
	}
	
	private JComponent createBottomComponent() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.CENTER));
		JButton executeQuery = new JButton("Execute");
		executeQuery.addActionListener(e -> {
			try {
				String query = queryPane.getText();
				SparqlResultSet result = reasoner.executeQuery(query);
				resultModel.setResults(result);
								
				
			}
			catch (SparqlReasonerException ex) {
				ErrorLogPanel.showErrorDialog(ex);
				JOptionPane.showMessageDialog(getOWLWorkspace(), ex.getMessage() + "\nSee the logs for more information.");
			}
		});
		
		JButton saveQueryResults = new JButton("Export Query Results");
		saveQueryResults.addActionListener(e -> {
			try {
				String query = queryPane.getText();
				SparqlResultSet result = reasoner.executeQuery(query);
				//resultModel.setResults(result);
	            
	           
			
	           
	            
				JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

		        //int returnValue = jfc.showOpenDialog(null);
		        int returnValue = jfc.showSaveDialog(null);

		        if (returnValue == JFileChooser.APPROVE_OPTION) {
		            File selectedFile = jfc.getSelectedFile();
		            //System.out.println(selectedFile.getAbsolutePath());
		            
		          
		            try {
		            
		            	BufferedWriter bw = new BufferedWriter(new FileWriter(selectedFile));
		            	
			            int rowCount = result.getRowCount();
			            int columnCount = result.getColumnCount();
			            
			            
			            for(int j=0; j < columnCount; j++) {         	
		            
		            	    bw.write(result.getColumnName(j) + "\t");
		            	        
			            	
			            }
			            
			            bw.write("\n");
			            
			            for(int i =0; i < rowCount; i++) {
			            	for(int j=0; j < columnCount; j++) {
			            		
			            		if(result.getResult(i, j) != null) {
			                   	        
			            			bw.write(result.getResult(i, j).toString() + "\t");
			            		
			            		}else {
			            			bw.write("\t");
			            		}
			            	}
			            	
			            	bw.write("\n");
			            }			            
		     
		        		            
		            	bw.close();
		            	
			            	
		            
		            }catch(IOException ie) {
		            	ie.printStackTrace();
		            }
		            
		            
		            
		        }
		        
		    
				
			}catch (SparqlReasonerException ex) {
				ErrorLogPanel.showErrorDialog(ex);
				JOptionPane.showMessageDialog(getOWLWorkspace(), ex.getMessage() + "\nSee the logs for more information.");
			}
		});
		
		panel.add(executeQuery);
		panel.add(saveQueryResults);
		return panel;
	}
	
	private JComponent createBottomResultComponent() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		JTextArea jtextarea = new JTextArea();
		
		JScrollPane jp = new JScrollPane(jtextarea);
		
		JButton executeQuery = new JButton("Execute");
		executeQuery.addActionListener(e -> {
			try {
				String query = queryPane.getText();
				SparqlResultSet result = reasoner.executeQuery(query);
				//resultModel.setResults(result);
				
				
				
				
	            int rowCount = result.getRowCount();
	            int columnCount = result.getColumnCount();
	            
	            StringBuffer sb = new StringBuffer();
	            for(int i =0; i < rowCount; i++) {
	            	for(int j=0; j < columnCount; j++) {
	            		
	            		String cell = (String)result.getResult(i, j);
	            		sb.append(cell + "\t");
	            		
	            	}
	            	sb.append("\n");
	            	
	            }
	            
	            jtextarea.setText(sb.toString());				
				
				
			}
			catch (SparqlReasonerException ex) {
				ErrorLogPanel.showErrorDialog(ex);
				JOptionPane.showMessageDialog(getOWLWorkspace(), ex.getMessage() + "\nSee the logs for more information.");
			}
		});
		
		panel.add(executeQuery);
		panel.add(jp);
		
		return panel;
		
	}
	

	@Override
	protected void disposeOWLView() {
		if (reasoner != null) {
			reasoner.dispose();
			reasoner = null;
		}
	}
	
	

}
