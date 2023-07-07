package nl.knaw.dans.vaultingest.core.utilities;

import nl.knaw.dans.vaultingest.core.deposit.Deposit;
import nl.knaw.dans.vaultingest.core.deposit.Outbox;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class TestOutbox implements Outbox {
    private final Path outboxPath;
    private final Map<Deposit, Path> movedTo = new HashMap<>();
    private final Map<Path, Deposit.State> movedPaths = new HashMap<>();

    public TestOutbox(Path outboxPath) {
        this.outboxPath = outboxPath;
    }

    public Map<Deposit, Path> getMovedTo() {
        return movedTo;
    }

    public Map<Path, Deposit.State> getMovedPaths() {
        return movedPaths;
    }

    @Override
    public void moveDeposit(Deposit deposit) throws IOException {
        movedTo.put(deposit, outboxPath);
    }

    @Override
    public void move(Path path, Deposit.State state) throws IOException {
        movedPaths.put(path, state);
    }

    @Override
    public Outbox withBatchDirectory(Path subPath) throws IOException {
        return new TestOutbox(outboxPath.resolve(subPath));
    }
}
