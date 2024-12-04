package com.atividade.appbank;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.swing.*;
import javax.swing.text.AbstractDocument;
import com.atividade.appbank.AccountUser.InterfaceUserLog;
import com.atividade.appbank.AccountUser.NumericLimitFilter;
import org.json.JSONObject;

public class AppBank extends javax.swing.JFrame {
    public static String numeroContaArmazenada;
    public static String respostaArmazenada;

    private int tentativas = 0;
    private Timer atualizacaoTimer;

    public AppBank() {
        initComponents();
        setIcon();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        AceAccount = new javax.swing.JButton();
        QBank = new javax.swing.JLabel();
        NAcount = new javax.swing.JLabel();
        NumberAcount = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("QBank");
        setResizable(false);

        AceAccount.setBackground(new java.awt.Color(16, 124, 65));
        AceAccount.setFont(new java.awt.Font("Poppins", 1, 12));
        AceAccount.setForeground(new java.awt.Color(255, 255, 255));
        AceAccount.setText("Acessar conta");
        AceAccount.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        AceAccount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AceAccountActionPerformed(evt);
            }
        });

        QBank.setFont(new java.awt.Font("Poppins", 1, 24));
        QBank.setForeground(new java.awt.Color(16, 124, 65));
        QBank.setText("QBank");

        NAcount.setFont(new java.awt.Font("Poppins", 1, 12));
        NAcount.setForeground(new java.awt.Color(16, 124, 65));
        NAcount.setText("N° da conta");

        NumberAcount.setSelectionColor(new java.awt.Color(16, 124, 65));
        ((AbstractDocument) NumberAcount.getDocument()).setDocumentFilter(new NumericLimitFilter(6));
        NumberAcount.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NumberAcountKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(293, 293, 293)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                        .addComponent(AceAccount, javax.swing.GroupLayout.PREFERRED_SIZE, 183,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(QBank)
                                        .addComponent(NAcount)
                                        .addComponent(NumberAcount, javax.swing.GroupLayout.PREFERRED_SIZE, 183,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(270, 270, 270)));
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGap(110, 110, 110)
                        .addComponent(QBank, javax.swing.GroupLayout.PREFERRED_SIZE, 37,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(NAcount, javax.swing.GroupLayout.PREFERRED_SIZE, 19,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(NumberAcount, javax.swing.GroupLayout.PREFERRED_SIZE, 33,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(AceAccount, javax.swing.GroupLayout.PREFERRED_SIZE, 38,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(235, Short.MAX_VALUE));

        pack();
    }

    private void AceAccountActionPerformed(java.awt.event.ActionEvent evt) {
        if (tentativas >= 3) {
            JOptionPane.showMessageDialog(this, "Muitas tentativas falhas. Aguarde 10 segundos para tentar novamente.",
                    "Bloqueado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String numeroConta = NumberAcount.getText().trim();
        if (numeroConta.isEmpty()) {
            mostrarErro("O campo N° da conta não pode estar vazio.");
            return;
        }

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                String resposta = null;
                try {
                    resposta = realizarRequisicao("http://localhost:8080/login/" + numeroConta);
                    if (resposta == null || resposta.isBlank() || resposta.equals("null")) {
                        mostrarErro("Usuário não encontrado. Verifique o número da conta.");
                        return null;
                    }
                    numeroContaArmazenada = numeroConta;
                    respostaArmazenada = resposta;

                    JSONObject jsonObject = new JSONObject(resposta);
                    JSONObject pessoaObject = jsonObject.optJSONObject("pessoa");

                    String nome = pessoaObject != null ? pessoaObject.optString("nome", "Usuário Desconhecido")
                            : "Usuário Desconhecido";
                    double saldo = jsonObject.optDouble("saldo", 0.0);
                    String tipoConta = jsonObject.optString("tipoConta", "Não especificado");

                    String saldoFormatado;
                    if (saldo == 0.0) {
                        saldoFormatado = "0,00";
                    } else {
                        java.text.DecimalFormat df = new java.text.DecimalFormat("#,###.00");
                        saldoFormatado = df.format(saldo).replace('.', ',');
                    }

                    InterfaceUserLog interfaceUserLog = new InterfaceUserLog();
                    interfaceUserLog.setTitle("Dashboard - " + nome);
                    interfaceUserLog.moneyuser.setText("Saldo: R$ " + saldoFormatado);
                    interfaceUserLog.nameuser.setText("Olá, " + nome);
                    interfaceUserLog.jLabel1.setText("Conta: " + tipoConta);

                    iniciarAtualizacaoAutomatica(interfaceUserLog, numeroConta);

                    interfaceUserLog.setVisible(true);
                    dispose();
                } catch (Exception e) {
                    mostrarErro("Erro ao acessar a conta. Tente novamente mais tarde.");
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void done() {
                super.done();
            }
        }.execute();
    }

    private void iniciarAtualizacaoAutomatica(InterfaceUserLog interfaceUserLog, String numeroConta) {
        if (atualizacaoTimer != null) {
            atualizacaoTimer.stop();
        }

        atualizacaoTimer = new Timer(2000, e -> {
            try {
                String respostaAtualizada = realizarRequisicao("http://localhost:8080/login/" + numeroConta);
                if (!respostaAtualizada.equals(respostaArmazenada)) {
                    respostaArmazenada = respostaAtualizada;
                    atualizarInterface(interfaceUserLog, respostaAtualizada);
                }
            } catch (Exception ex) {
                System.err.println("Erro durante a atualização automática: " + ex.getMessage());
            }
        });

        atualizacaoTimer.start();
    }

    private void atualizarInterface(InterfaceUserLog interfaceUserLog, String respostaAtualizada) {
        JSONObject jsonObject = new JSONObject(respostaAtualizada);
        JSONObject pessoaObject = jsonObject.optJSONObject("pessoa");

        String nome = pessoaObject != null ? pessoaObject.optString("nome", "Usuário Desconhecido")
                : "Usuário Desconhecido";
        double saldo = jsonObject.optDouble("saldo", 0.0);
        String tipoConta = jsonObject.optString("tipoConta", "Não especificado");

        interfaceUserLog.setTitle("Dashboard - " + nome);
        interfaceUserLog.moneyuser.setText("Saldo: R$ " + String.format("%.2f", saldo));
        interfaceUserLog.nameuser.setText("Olá, " + nome);
        interfaceUserLog.jLabel1.setText("Conta: " + tipoConta);
    }

    private void NumberAcountKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_V)
            evt.consume();
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
            AceAccount.doClick();
    }

    private String realizarRequisicao(String urlString) throws Exception {
        HttpURLConnection con = (HttpURLConnection) new URL(urlString).openConnection();
        con.setRequestMethod("GET");
        if (con.getResponseCode() != 200)
            throw new RuntimeException("Erro ao acessar API");

        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null)
                response.append(line);
            return response.toString();
        }
    }

    private void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
        tentativas++;
        if (tentativas >= 3) {
            JOptionPane.showMessageDialog(this, "Muitas tentativas falhas. Aguarde 10 segundos para tentar novamente.",
                    "Bloqueio Temporário", JOptionPane.WARNING_MESSAGE);
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            tentativas = 0;
        }
    }

    private void setIcon() {
        try {
            String iconPath = "src/main/java/com/atividade/appbank/icons/logo.png";
            setIconImage(new ImageIcon(iconPath).getImage());
        } catch (Exception e) {
            System.out.println("Erro ao carregar o ícone: " + e.getMessage());
        }
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AppBank().setVisible(true);
            }
        });
    }

    private javax.swing.JButton AceAccount;
    private javax.swing.JLabel QBank;
    private javax.swing.JLabel NAcount;
    private javax.swing.JTextField NumberAcount;
}
