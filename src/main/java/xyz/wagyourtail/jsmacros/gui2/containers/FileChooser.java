package xyz.wagyourtail.jsmacros.gui2.containers;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.function.Consumer;

import xyz.wagyourtail.jsmacros.jsMacros;
import xyz.wagyourtail.jsmacros.gui2.elements.Button;
import xyz.wagyourtail.jsmacros.gui2.elements.OverlayContainer;
import xyz.wagyourtail.jsmacros.gui2.elements.Scrollbar;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.StringRenderable;
import net.minecraft.util.Util;

public class FileChooser extends OverlayContainer {
    private File directory;
    private StringRenderable dirname;
    private File selected;
    private Button select;
    private Button rename;
    private Button delete;
    private Button newbtn;
    private Button openf;
    private Button editbtn;
    private ArrayList<fileObj> files = new ArrayList<>();
    private Consumer<File> setFile;
    private int topScroll;

    public FileChooser(int x, int y, int width, int height, TextRenderer textRenderer, File directory, Consumer<AbstractButtonWidget> addButton, Consumer<AbstractButtonWidget> removeButton, Consumer<OverlayContainer> close, Consumer<File> setFile) {
        super(x, y, width, height, textRenderer, addButton, removeButton, close);
        this.setFile = setFile;
        this.directory = directory;
    }

    public void setDir(File dir) {
        for (fileObj f : files) {
            this.removeButton(f.btn);
        }
        files.clear();
        this.directory = dir;
        this.dirname = new LiteralText("." + dir.getAbsolutePath().substring(jsMacros.config.macroFolder.getAbsolutePath().length()).replaceAll("\\\\", "/"));
        
        if (!this.directory.equals(jsMacros.config.macroFolder)) {
            addFile(this.directory.getParentFile(), "..");
        }
        
        ArrayList<File> files = new ArrayList<File>(Arrays.asList(directory.listFiles()));
        Collections.sort(files, new sortFile());
        for (File f : files) {
            addFile(f);
        }
    }

    public void selectFile(File f) {
        if (f.isDirectory()) {
            this.setDir(f);
        } else {
            this.selected = f;
        }
        for (fileObj fi : files) {
            if (f.equals(fi.file)) {
                fi.btn.setColor(0x7FFFFFFF);
            } else {
                fi.btn.setColor(0);
            }
        }
    }

    public void init() {
        super.init();
        int w = width - 4;
        topScroll = y + 13;
        this.addButton(new Button(x + width - 12, y + 2, 10, 10, 0, 0x7FFFFFFF, 0x7FFFFFFF, 0xFFFFFF, new LiteralText("X"), (btn) -> {
            this.close();
        }));
        scroll = (Scrollbar) this.addButton(new Scrollbar(x + width - 10, y + 13, 8, height - 28, 0, 0xFF000000, 0xFFFFFFFF, 2, this::onScrollbar));

        select = (Button) this.addButton(new Button(x + w * 5 / 6 + 2, y + height - 14, w / 6, 12, 0, 0, 0x7FFFFFFF, 0xFFFFFF, new LiteralText("Select"), (btn) -> {
            if (this.selected != null && this.setFile != null) {
                this.setFile.accept(this.selected);
                this.close();
            }
        }));
        
        editbtn = (Button) this.addButton(new Button(x + w * 4 / 6 + 2, y + height - 14, w / 6, 12, 0, 0, 0x7FFFFFFF, 0xFFFFFF, new LiteralText("Edit"), (btn) -> {
            if (this.selected != null) Util.getOperatingSystem().open(this.selected);
        }));
//        editbtn.visible = false;
        
        rename = (Button) this.addButton(new Button(x + w * 3 / 6 + 2, y + height - 14, w / 6, 12, 0, 0, 0x7FFFFFFF, 0xFFFFFF, new LiteralText("Rename"), (btn) -> {

        }));
        rename.visible = false;
        
        delete = (Button) this.addButton(new Button(x + w * 2 / 6 + 2, y + height - 14, w / 6, 12, 0, 0, 0x7FFFFFFF, 0xFFFFFF, new LiteralText("Delete"), (btn) -> {

        }));
        delete.visible = false;
        
        newbtn = (Button) this.addButton(new Button(x + w * 1 / 6 + 2, y + height - 14, w / 6, 12, 0, 0, 0x7FFFFFFF, 0xFFFFFF, new LiteralText("New"), (btn) -> {

        }));
        newbtn.visible = false;
        
        openf = (Button) this.addButton(new Button(x + 2, y + height - 14, w / 6, 12, 0, 0, 0x7FFFFFFF, 0xFFFFFF, new LiteralText("Open Folder"), (btn) -> {
            Util.getOperatingSystem().open(directory);
        }));
        
        this.setDir(directory);
    }
    
    public void addFile(File f) {
        addFile(f, f.getName());
    }
    
    public void addFile(File f, String btnText) {
        fileObj file = new fileObj(f, new Button(x + 3 + (files.size() % 5 * (width - 12) / 5), topScroll + (files.size() / 5 * 12), (width - 12) / 5, 12, 0, 0, 0x7FFFFFFF, f.isDirectory() ? 0xFFFF00 : 0xFFFFFF, new LiteralText(btnText), (btn) -> {
            selectFile(f);
        }));
        files.add(file);
        this.addButton(file.btn);
        scroll.setScrollPages((files.size() / 5 * 12) / Math.max(1, height - 27));
    }

    public void onScrollbar(double page) {
        topScroll = y + 13 - (int) (page * (height - 27));
        int i = 0;
        for (fileObj fi : files) {
            if (topScroll + (i / 5 * 12) < y + 13 || topScroll + (i / 5 * 12) > y + height - 27) fi.btn.visible = false;
            else fi.btn.visible = true;
            fi.btn.setPos(x + 3 + (i % 5 * (width - 12) / 5), topScroll + (i / 5 * 12), (width - 12) / 5, 12);
            ++i;
        }
    }

    public void renderBackground(MatrixStack matricies) {
        // black bg
        fill(matricies, x, y, x + width, y + height, 0xFF000000);
        // 2 layer border
        fill(matricies, x, y, x + width, y + 1, 0x7F7F7F7F);
        fill(matricies, x, y + height - 1, x + width, y + height, 0x7F7F7F7F);
        fill(matricies, x, y + 1, x + 1, y + height - 1, 0x7F7F7F7F);
        fill(matricies, x + width - 1, y + 1, x + width, y + height - 1, 0x7F7F7F7F);

        fill(matricies, x + 1, y + 1, x + width - 1, y + 2, 0xFFFFFFFF);
        fill(matricies, x + 1, y + height - 2, x + width - 1, y + height - 1, 0xFFFFFFFF);
        fill(matricies, x + 1, y + 1, x + 2, y + height - 1, 0xFFFFFFFF);
        fill(matricies, x + width - 2, y + 1, x + width - 1, y + height - 1, 0xFFFFFFFF);

    }

    public void render(MatrixStack matricies, int mouseX, int mouseY, float delta) {
        renderBackground(matricies);
        
        textRenderer.drawTrimmed(this.dirname, x+3, y+3,width-14, 0xFFFFFF);
        
        fill(matricies, x + 2, y + 12, x + width - 2, y + 13, 0xFFFFFFFF);
        fill(matricies, x + 2, y + height - 15, x + width - 2, y + height - 14, 0xFFFFFFFF);
//        textRenderer.draw(, mouseX, mouseY, color, shadow, matrix, vertexConsumers, seeThrough, backgroundColor, light)
        super.render(matricies, mouseX, mouseY, delta);
    }

    public static class fileObj {
        public File file;
        public Button btn;

        public fileObj(File file, Button btn) {
            this.file = file;
            this.btn = btn;
        }
    }

    public static class sortFile implements Comparator<File> {
        public int compare(File a, File b) {
            if (a.isDirectory() && !b.isDirectory()) {
                return -1;
            } else if (!a.isDirectory() && b.isDirectory()) {
                return 1;
            } else {
                return a.getName().compareTo(b.getName());
            }
        }
    }
}