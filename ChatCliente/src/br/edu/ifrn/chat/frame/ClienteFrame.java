package br.edu.ifrn.chat.frame;

import br.edu.ifrn.chat.model.Pacote;
import br.edu.ifrn.chat.enumarator.Comando;
import br.edu.ifrn.chat.service.ClienteServico;
import java.awt.HeadlessException;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;

import static javax.swing.JOptionPane.PLAIN_MESSAGE;
import static javax.swing.JOptionPane.showInputDialog;

public class ClienteFrame extends javax.swing.JFrame {

    private Socket socket;
    private ClienteServico servico;
    private Pacote pacote;
    private String nome;
    private String IP_Servidor;
    private int Porta_Servidor;

    /**
     * Creates new form ClientFrame
     */
    public ClienteFrame() {
        try {
            conectar();
            initComponents();
        } catch (HeadlessException e) {
            System.exit(0);
        }

    }

    private void conectar() throws HeadlessException {
        String ip = showInputDialog(null, "Informe o IP do Servidor: ", "", PLAIN_MESSAGE);
        if (ip != null) {
            this.IP_Servidor = ip;
        } else {
            // Servidor Padrão.
            this.IP_Servidor = "localhost";
        }

        String porta = showInputDialog(null, "Informe a Porta do Servidor: ", "", PLAIN_MESSAGE);
        if (porta != null) {
            this.Porta_Servidor = Integer.parseInt(porta);
        } else {
            //Porta do Servidor Padrão.
            this.Porta_Servidor = 7896;
        }
        
        String nickName = showInputDialog(null, "Entre com seu nick-name: ", "", PLAIN_MESSAGE); 
        if (nickName != null){
            this.nome = nickName;
        } else {
            this.nome = "Visitante-"+(int) (Math.random() * 101);
        }
         

        super.setTitle("Bate-papo utilizado por " + this.nome);

        this.servico = new ClienteServico();
        this.socket = this.servico.connect(this.IP_Servidor, this.Porta_Servidor);
        new Thread(new Listener(this.socket)).start();

        this.pacote = new Pacote();
        this.pacote.setUsuario(this.nome);
        this.pacote.setMensagem(this.nome + " entrou no bate-papo\n");
        this.pacote.setAcao(Comando.CONECTAR);

        this.servico.enviar(this.pacote);
    }

    private void desconectar() {
        this.pacote = new Pacote();
        this.pacote.setUsuario(this.nome);
        this.pacote.setAcao(Comando.DESCONECTAR);
        this.servico.enviar(this.pacote);
    }

    private void desconectado() {
        try {
            this.socket.close();
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void falhaConexao() {
        JOptionPane.showMessageDialog(null, "Conexão Falhou!\nEntre com outro nick-name.", "Falha na Conexão...", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }

    private void recebido(Pacote pacote) {
        this.txtReceived.append(pacote.getUsuario() + " diz: " + pacote.getMensagem() + "\n");
    }

    private void statusRecebido(Pacote pacote) {
        this.pacote = pacote;
        this.txtReceived.append(pacote.getMensagem());
    }

    private void atualizar(Pacote pacote) {
        Set<String> usuarios = pacote.getUsuarioOnLine();
        usuarios.remove(pacote.getUsuario());
        String[] array = usuarios.toArray(new String[usuarios.size()]);
        this.lstOnlines.setListData(array);
        this.lstOnlines.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.lstOnlines.setLayoutOrientation(JList.VERTICAL);
        System.out.println(pacote.getUsuarioOnLine().toString());
    }

    private void enviar() {
        String mensagem = this.txtSend.getText();
        if (!mensagem.isEmpty()) {
            this.pacote = new Pacote();
            if (this.lstOnlines.getSelectedIndex() > -1) {
                this.pacote.setUsuarioReservado((String) this.lstOnlines.getSelectedValue());
                this.pacote.setAcao(Comando.ENVIAR_RESERVADO);
                this.lstOnlines.clearSelection();
            } else {
                this.pacote.setAcao(Comando.ENVIAR_TODOS);
            }
            this.pacote.setUsuario(this.nome);
            this.pacote.setMensagem(mensagem);
            this.txtReceived.append("Você disse: " + mensagem + "\n");
            this.servico.enviar(this.pacote);
        }
        this.txtSend.setText("");
    }

    private class Listener implements Runnable {

        private ObjectInputStream in;

        public Listener(Socket socket) {
            try {
                this.in = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        @Override
        public void run() {
            Pacote pacote = null;
            try {
                while ((pacote = (Pacote) in.readObject()) != null) {
                    Comando comando = pacote.getAcao();
                    switch (comando) {
                        case CONECTADO:
                            statusRecebido(pacote);
                            break;
                        case DESCONECTADO:
                            desconectado();
                            break;
                        case NAO_CONECTADO:
                            falhaConexao();
                            break;
                        case RECEBIDO:
                            recebido(pacote);
                            break;
                        case ENVIAR_TODOS:
                            statusRecebido(pacote);
                            break;
                        case USUARIOS_ONLINE:
                            atualizar(pacote);
                            break;
                        default:
                    }
                }
            } catch (IOException e) {
                desconectado();
                System.out.println(e.getMessage());
            } catch (ClassNotFoundException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtReceived = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        lstOnlines = new javax.swing.JList();
        jLabel2 = new javax.swing.JLabel();
        btnSend = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtSend = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jLabel1.setText("Mensagens:");

        txtReceived.setEditable(false);
        txtReceived.setColumns(20);
        txtReceived.setRows(5);
        jScrollPane1.setViewportView(txtReceived);

        jScrollPane2.setViewportView(lstOnlines);

        jLabel2.setText("On-lines");

        btnSend.setText("Enviar");
        btnSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSendActionPerformed(evt);
            }
        });

        txtSend.setColumns(20);
        txtSend.setRows(5);
        jScrollPane3.setViewportView(txtSend);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 413, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 413, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnSend, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(76, 355, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSend))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 318, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSendActionPerformed
        enviar();
    }//GEN-LAST:event_btnSendActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        desconectar();
    }//GEN-LAST:event_formWindowClosing

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSend;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JList lstOnlines;
    private javax.swing.JTextArea txtReceived;
    private javax.swing.JTextArea txtSend;
    // End of variables declaration//GEN-END:variables
}
