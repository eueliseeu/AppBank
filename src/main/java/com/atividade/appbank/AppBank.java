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
    private static final int MAX_TENTATIVAS = 3;
    private static final int TEMPO_BLOQUEIO_MS = 10000;
    private static final String API_URL = "http://localhost:8080/login/";
    private static final String MSG_ERRO_API = "Erro ao acessar API";
    private static final String MSG_USUARIO_NAO_ENCONTRADO = "Usuário não encontrado. Verifique o número da conta.";

    public static String numeroContaArmazenada;
    public static String respostaArmazenada;

    private int tentativas = 0;

    public AppBank() {
        initUI();
        setIcon();
        setLocationRelativeTo(null);
    }

    private void initUI() {
        AceAccount = new JButton("Acessar conta");
        QBank = new JLabel("QBank");
        NAcount = new JLabel("N° da conta");
        NumberAcount = new JTextField();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("QBank");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        AceAccount.setBackground(new java.awt.Color(16, 124, 65));
        AceAccount.setFont(new java.awt.Font("Poppins", 1, 12));
        AceAccount.setForeground(java.awt.Color.WHITE);
        AceAccount.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        AceAccount.addActionListener(evt -> acessarConta());

        QBank.setFont(new java.awt.Font("Poppins", 1, 24));
        QBank.setForeground(new java.awt.Color(16, 124, 65));

        NAcount.setFont(new java.awt.Font("Poppins", 1, 12));
        NAcount.setForeground(new java.awt.Color(16, 124, 65));

        NumberAcount.setSelectionColor(new java.awt.Color(16, 124, 65));
        ((AbstractDocument) NumberAcount.getDocument()).setDocumentFilter(new NumericLimitFilter(6));
        NumberAcount.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                if (evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_V) evt.consume();
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) AceAccount.doClick();
            }
        });

        var layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addGroup(layout.createSequentialGroup()
                        .addGap(293)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                .addComponent(AceAccount, 183, 183, 183)
                                .addComponent(QBank)
                                .addComponent(NAcount)
                                .addComponent(NumberAcount, 183, 183, 183))
                        .addGap(270)));
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGap(110)
                .addComponent(QBank, 37, 37, 37)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(NAcount, 19, 19, 19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(NumberAcount, 33, 33, 33)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(AceAccount, 38, 38, 38)
                .addContainerGap(235, Short.MAX_VALUE));
    }

    private void acessarConta() {
        if (tentativas >= MAX_TENTATIVAS) {
            JOptionPane.showMessageDialog(this, "Muitas tentativas falhas. Aguarde 10 segundos para tentar novamente.", "Bloqueado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String numeroConta = NumberAcount.getText().trim();
        if (numeroConta.isEmpty()) {
            mostrarErro("O campo N° da conta não pode estar vazio.");
            return;
        }

        try {
            String resposta = realizarRequisicao(API_URL + numeroConta);
            if (resposta == null || resposta.isBlank() || resposta.equals("null")) {
                mostrarErro(MSG_USUARIO_NAO_ENCONTRADO);
                return;
            }

            numeroContaArmazenada = numeroConta;
            respostaArmazenada = resposta;

            JSONObject jsonObject = new JSONObject(resposta);
            JSONObject pessoaObject = jsonObject.optJSONObject("pessoa");

            String nome = pessoaObject != null ? pessoaObject.optString("nome", "Usuário Desconhecido") : "Usuário Desconhecido";
            double saldo = jsonObject.optDouble("saldo", 0.0);
            String tipoConta = jsonObject.optString("tipoConta", "Não especificado");

            InterfaceUserLog interfaceUserLog = new InterfaceUserLog();
            interfaceUserLog.setTitle("Dashboard - " + nome);
            interfaceUserLog.moneyuser.setText("Saldo: R$ " + String.format("%.2f", saldo));
            interfaceUserLog.nameuser.setText("Olá, " + nome);
            interfaceUserLog.jLabel1.setText("Conta: " + tipoConta);

            interfaceUserLog.setVisible(true);
            dispose();
        } catch (Exception e) {
            mostrarErro(MSG_ERRO_API + ": " + e.getMessage());
        }
    }

    private String realizarRequisicao(String urlString) throws Exception {
        HttpURLConnection con = (HttpURLConnection) new URL(urlString).openConnection();
        con.setRequestMethod("GET");
        if (con.getResponseCode() != 200) throw new RuntimeException(MSG_USUARIO_NAO_ENCONTRADO);

        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) response.append(line);
            return response.toString();
        }
    }

    private void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
        if (++tentativas >= MAX_TENTATIVAS) bloquearBotao();
    }

    private void bloquearBotao() {
        AceAccount.setEnabled(false);
        new Timer(TEMPO_BLOQUEIO_MS, e -> {
            tentativas = 0;
            AceAccount.setEnabled(true);
        }).start();
    }

    private void setIcon() {
        try {
            String iconPath = "src/main/java/com/atividade/appbank/icons/logo.png";
            setIconImage(new ImageIcon(iconPath).getImage());
        } catch (Exception e) {
            System.out.println("Erro ao carregar o ícone: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AppBank().setVisible(true));
    }

    private JButton AceAccount;
    private JLabel NAcount;
    private JTextField NumberAcount;
    private JLabel QBank;
}
