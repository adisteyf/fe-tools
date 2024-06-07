import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.io.*;
import java.nio.FloatBuffer;
import java.util.Objects;
import java.util.Scanner;

import static org.lwjgl.opengl.GL20.*;

public class Shader {
    private int program;
    private int vs;
    private int fs;
    private boolean startReadFragment = false;
    
    public Shader(String filename) {
        program = glCreateProgram();

        vs = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vs, readFile(filename));
        glCompileShader(vs);
        if (glGetShaderi(vs, GL_COMPILE_STATUS) != 1) {
            System.err.println(glGetShaderInfoLog(vs));
            System.exit(1);
        }

        fs = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fs, readFile(filename));
        glCompileShader(fs);
        if (glGetShaderi(fs, GL_COMPILE_STATUS) != 1) {
            System.err.println(glGetShaderInfoLog(fs));
            System.exit(1);
        }
        glAttachShader(program, vs);
        glAttachShader(program, fs);

        glBindAttribLocation(program, 0, "vertices");
        glBindAttribLocation(program, 1, "textures");

        glLinkProgram(program);
        if (glGetProgrami(program, GL_LINK_STATUS) != 1) {
            System.err.println(glGetProgramInfoLog(program)+"FROM GL_LINK_STATUS");
            System.exit(1);
        }
        glValidateProgram(program);
        if (glGetProgrami(program, GL_VALIDATE_STATUS) != 1) {
            System.err.println(glGetProgramInfoLog(program)+" FROM GL_VALIDATE_STATUS");
            System.exit(1);
        }
    }

    public void bind() {
        glUseProgram(program);
    }

    public void setUniform(String name, int val) {
        int location = glGetUniformLocation(program, name);
        if (location != -1)
            glUniform1i(location, val);
    }

    public void setUniform(String name, Matrix4f val) {
        int location = glGetUniformLocation(program, name);
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        val.get(buffer);
        if (location != -1)
            glUniformMatrix4fv(location, false, buffer);
    }

    private String readFile(String filename) {
        StringBuilder string = new StringBuilder();
        Scanner br;
        boolean stop = false;
        try {
            br = new Scanner(new File("./assets/shaders/"+filename));
            if (!startReadFragment) {
                while (br.hasNextLine() && !stop) {
                    String line = br.nextLine();

                    string.append(line).append("\n");

                    if (Objects.equals(line, "//fragment shader")) {
                        stop = true;
                    }
                }
                br.close();
            } else {
                boolean fragmentPart = false;
                while (br.hasNextLine()) {
                    String line = br.nextLine();
                    if (Objects.equals(line, "//fragment shader"))
                        fragmentPart=true;
                    if (fragmentPart)
                        string.append(line).append("\n");
                }
                br.close();
            }
        } catch (IOException e) {
            System.out.println("ERROR DURING READING SHADER");
            e.printStackTrace();
        }
        startReadFragment = true;
        return string.toString();
    }
}
