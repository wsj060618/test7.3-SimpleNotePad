import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.io.File;

public class SimpleNotePad {
    private JFrame jf; // 面板
    private JTextArea jta; // 多行文本域
    private JMenuBar jmb; // 菜单栏
    private JMenu jm_file; // 文件菜单
    private JMenuItem jmi_new; // 新建文件菜单项
    private JMenuItem jmi_open; // 打开文件菜单项
    private JMenuItem jmi_save; // 保存文件菜单项
    private JMenuItem jmi_saveAs; // 另存为菜单项
    private JMenuItem jmi_exit; // 退出文件菜单项
    private JMenu jm_edit; // 编辑菜单
    private JMenuItem jmi_cancel; // 撤销编辑菜单项
    private JMenu jm_look; // 查看菜单
    private JCheckBoxMenuItem jmi_wordwrap;// 自动换行菜单项
    private JFileChooser jfc; // 文件选择器
    private File currentFile; // 当前打开的文件
    private boolean ctrlPressed = false; // 跟踪Ctrl键是否按下
    private JMenuItem jmi_zoomIn; // 放大菜单项
    private JMenuItem jmi_zoomOut; // 缩小菜单项
    //初始化窗口
    public void InitFrame() {
        // 初始化窗口
        jf = new JFrame("简易记事本");
        jf.setSize(800,600);
        jf.setLocationRelativeTo(null); // 窗口居中
        jf.setBackground(Color.WHITE);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 初始化菜单栏
        jmb = new JMenuBar();
        jm_file = new JMenu("文件");
        jmi_new = new JMenuItem("新建");
        jmi_open = new JMenuItem("打开");
        jmi_save = new JMenuItem("保存");
        jmi_saveAs = new JMenuItem("另存为"); // 添加另存为菜单项
        jmi_exit = new JMenuItem("退出");
        jm_file.add(jmi_new);
        jm_file.add(jmi_open);
        jm_file.add(jmi_save);
        jm_file.add(jmi_saveAs); // 添加到菜单
        jm_file.addSeparator(); // 添加分隔线
        jm_file.add(jmi_exit);
        jmb.add(jm_file);
        jm_edit = new JMenu("编辑");
        jmi_cancel = new JMenuItem("撤销");
        jm_edit.add(jmi_cancel);
        jmb.add(jm_edit);
        jm_look = new JMenu("查看");
        jmi_wordwrap = new JCheckBoxMenuItem("自动换行");
        jmi_zoomIn = new JMenuItem("放大");
        jmi_zoomOut = new JMenuItem("缩小");
        jmi_wordwrap.setSelected(true); // 默认选中
        jm_look.add(jmi_wordwrap);
        jm_look.add(jmi_zoomIn);
        jm_look.add(jmi_zoomOut);
        jm_look.addSeparator(); // 添加分隔线
        jmb.add(jm_look);
        jf.setJMenuBar(jmb);

        // 初始化滚动面板和文本域
        jta = new JTextArea();
        jta.setEditable(true); // 确保文本域可编辑
        jta.setLineWrap(true); // 设置自动换行
        jta.setFont(new Font("微软雅黑", Font.PLAIN, 14)); // 设置字体
        JScrollPane jsp = new JScrollPane(jta);
        jf.add(jsp);
        InitListener(); // 初始化监听器
        jf.setVisible(true);
    }

    // 初始化监听器
    public void InitListener() {
        // 初始化新建文件监听器
        jmi_new.addActionListener(e -> {
            jta.setText(""); // 新建文件时清空文本域
            currentFile = null; // 清除当前文件引用
            jf.setTitle("简易记事本 - 未命名"); // 重置窗口标题
            // 提示用户新建文件成功
            JOptionPane.showMessageDialog(jf, "新建文件成功", "提示", JOptionPane.INFORMATION_MESSAGE);
        });

        // 初始化打开文件监听器
        jmi_open.addActionListener(e -> {
            // 提示用户打开文件
            jfc = new JFileChooser();
            int ret = jfc.showOpenDialog(jf); // 显示打开文件对话框
            if (ret == JFileChooser.APPROVE_OPTION) { // 用户点击了打开按钮
                try {
                    currentFile = jfc.getSelectedFile(); // 保存当前打开的文件
                    jf.setTitle("简易记事本 - " + currentFile.getName()); // 更新窗口标题
                    // 读取文件内容到文本域
                    jta.setText(new String(Files.readAllBytes(currentFile.toPath()), StandardCharsets.UTF_8));
                } catch (IOException ex) {
                    ex.printStackTrace();
                    // 提示用户打开文件失败
                    JOptionPane.showMessageDialog(jf, "打开文件失败", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 初始化保存文件监听器
        jmi_save.addActionListener(e -> {
            // 如果有当前打开的文件，直接保存
            if (currentFile != null) {
                try {
                    Files.write(currentFile.toPath(), jta.getText().getBytes(StandardCharsets.UTF_8));
                    JOptionPane.showMessageDialog(jf, "文件保存成功", "提示", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(jf, "保存文件失败", "错误", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // 没有当前文件，执行另存为操作
                saveAsFile();
            }
        });

        // 初始化另存为监听器
        jmi_saveAs.addActionListener(e -> {
            saveAsFile();
        });

        // 初始化退出监听器
        jmi_exit.addActionListener(e -> {
            System.exit(0); // 退出应用程序
        });

        // 初始化撤销监听器
        jmi_cancel.addActionListener(e -> {
            // 撤销操作，简单地清空文本域
            jta.setText("");
        });
        // 为撤销操作绑定ctrl + z快捷键
        jmi_cancel.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK));
        // 处理自动换行功能
        jmi_wordwrap.addActionListener(e -> {
            jta.setLineWrap(jmi_wordwrap.isSelected());
        });

        // 添加字体放大缩小功能
        jmi_zoomIn.addActionListener(e -> zoomIn());
        jmi_zoomOut.addActionListener(e -> zoomOut());
        // 为字体放大操作绑定ctrl + =快捷键
        jmi_zoomIn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, KeyEvent.CTRL_DOWN_MASK));
        // 为字体缩小操作绑定ctrl + -快捷键
        jmi_zoomOut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, KeyEvent.CTRL_DOWN_MASK));
    }
    
    // 另存为文件方法
    private void saveAsFile() {
        jfc = new JFileChooser();
        int ret = jfc.showSaveDialog(jf); // 显示保存文件对话框
        if (ret == JFileChooser.APPROVE_OPTION) { // 用户点击了保存按钮
            try {
                currentFile = jfc.getSelectedFile(); // 更新当前文件
                jf.setTitle("简易记事本 - " + currentFile.getName()); // 更新窗口标题
                Files.write(currentFile.toPath(), jta.getText().getBytes(StandardCharsets.UTF_8));
                JOptionPane.showMessageDialog(jf, "文件保存成功", "提示", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(jf, "保存文件失败", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // 字体放大方法
    private void zoomIn() {
        Font currentFont = jta.getFont();
        int currentSize = currentFont.getSize();
        String fontFamily = currentFont.getFamily(); // 获取当前字体的字体名称
        int fontStyle = currentFont.getStyle();

        jta.setFont(new Font(fontFamily, fontStyle, currentSize + 2)); // 每次增加2点字号
    }

    // 字体缩小方法
    private void zoomOut() {
        Font currentFont = jta.getFont();
        int currentSize = currentFont.getSize();
        String fontFamily = currentFont.getFamily(); // 获取当前字体的字体名称
        int fontStyle = currentFont.getStyle();

        // 设置最小字体为8号，避免字体过小无法阅读
        int newSize = Math.max(8, currentSize - 2);
        jta.setFont(new Font(fontFamily, fontStyle, newSize));
    }

    public SimpleNotePad() {
        InitFrame();
    }
    public static void main(String[] args) {
        new SimpleNotePad();
    }
}