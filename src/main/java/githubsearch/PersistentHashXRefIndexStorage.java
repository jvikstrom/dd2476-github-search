package githubsearch;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class PersistentHashXRefIndexStorage extends HashXRefIndexStorage {
    private final String PACKAGES_PATH = "packages.dat";
    private final String METHOD_DECL_PATH = "method_decls.dat";
    private final String CALL_EXPRS_PATH = "call_exprs.dat";
    private final String RESOLVED_CALLS_PATH = "resolved_calls.dat";

    private String folder;
    private boolean allFilesExist() {
        if(!Files.exists(new File(PACKAGES_PATH).toPath())) {
            return false;
        }
        if(!Files.exists(new File(METHOD_DECL_PATH).toPath())) {
            return false;
        }
        if(!Files.exists(new File(CALL_EXPRS_PATH).toPath())) {
            return false;
        }
        return Files.exists(new File(RESOLVED_CALLS_PATH).toPath());
    }
    public PersistentHashXRefIndexStorage(String folder) throws IOException, ClassNotFoundException {
        this.folder = folder;
        if(allFilesExist()) {
            System.out.println("Reading persisted XRef data");
            FileInputStream fis = new FileInputStream(folder+PACKAGES_PATH);
            ObjectInputStream ois = new ObjectInputStream(fis);
            this.packages = (HashMap<String,SymbolPackage>)ois.readObject();
            ois.close();
            fis.close();
            fis = new FileInputStream(folder+METHOD_DECL_PATH);
            ois = new ObjectInputStream(fis);
            this.methodDecls = (HashMap<String, List<MethodDecl>>)ois.readObject();
            ois.close();
            fis.close();
            fis = new FileInputStream(folder+CALL_EXPRS_PATH);
            ois = new ObjectInputStream(fis);
            this.callExprs = (HashMap<String, List<CallExpr>>)ois.readObject();
            ois.close();
            fis.close();
            fis = new FileInputStream(folder+RESOLVED_CALLS_PATH);
            ois = new ObjectInputStream(fis);
            this.resolvedCalls = (HashMap<MethodDecl, Set<CallExpr>>)ois.readObject();
            ois.close();
            fis.close();
            System.out.println("Done reading XRef data");
        }
    }

    @Override
    public void flush() throws IOException{
        System.out.println("Flushing XRef data");
        FileOutputStream fos = new FileOutputStream(folder+PACKAGES_PATH);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(packages);
        oos.close();
        fos.close();
        fos = new FileOutputStream(folder+METHOD_DECL_PATH);
        oos = new ObjectOutputStream(fos);
        oos.writeObject(methodDecls);
        oos.close();
        fos.close();
        fos = new FileOutputStream(folder+CALL_EXPRS_PATH);
        oos = new ObjectOutputStream(fos);
        oos.writeObject(callExprs);
        oos.close();
        fos.close();
        fos = new FileOutputStream(folder+RESOLVED_CALLS_PATH);
        oos = new ObjectOutputStream(fos);
        oos.writeObject(resolvedCalls);
        oos.close();
        fos.close();
        System.out.println("Done flushing XRef data");
    }
}
