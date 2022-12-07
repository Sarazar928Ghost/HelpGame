package org.starloco.locos.database.statics.data;

import com.zaxxer.hikari.HikariDataSource;
import org.starloco.locos.database.statics.AbstractDAO;
import org.starloco.locos.entity.pet.PetEntry;
import org.starloco.locos.exchange.transfer.DataQueue;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PetData extends AbstractDAO<PetEntry> {

    public PetData(HikariDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void load(Object obj) {}

    @Override
    public boolean update(PetEntry pets) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE `world.entity.pets` SET `lastEatDate` = ?, `quantityEat` = ?, `pdv` = ?, `corpulence` = ?, `isEPO` = ? WHERE `id` = ?;");
            p.setLong(1, pets.getLastEatDate());
            p.setInt(2, pets.getQuaEat());
            p.setInt(3, pets.getPdv());
            p.setInt(4, pets.getCorpulence());
            p.setInt(5, (pets.getIsEupeoh() ? 1 : 0));
            p.setInt(6, pets.getObjectId());
            execute(p);
            return true;
        } catch (SQLException e) {
            super.sendError("PetData update", e);
        } finally {
            close(p);
        }
        return false;
    }

    public int load() {
        Result result = null;
        int i = 0;
        try {
            result = getData("SELECT * FROM `world.entity.pets`;");
            ResultSet RS = result.resultSet;
            while (RS.next()) {
                i++;
                World.world.addPetsEntry(new PetEntry(RS.getInt("id"), RS.getInt("template"), RS.getLong("lastEatDate"), RS.getInt("quantityEat"), RS.getInt("pdv"), RS.getInt("corpulence"), (RS.getInt("isEPO") == 1)));
            }
        } catch (SQLException e) {
            super.sendError("PetData load", e);
        } finally {
            close(result);
        }
        return i;
    }

    public void add(int id, long lastEatDate, int template) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("INSERT INTO `world.entity.pets`(`id`, `template`, `lastEatDate`, `quantityEat`, `pdv`, `corpulence`, `isEPO`) VALUES (?, ?, ?, ?, ?, ?, ?);");
            p.setInt(1, id);
            p.setInt(2, template);
            p.setLong(3, lastEatDate);
            p.setInt(4, 0);
            p.setInt(5, 10);
            p.setInt(6, 0);
            p.setInt(7, 0);
            execute(p);
        } catch (SQLException e) {
            super.sendError("PetData add", e);
        } finally {
            close(p);
        }
    }

    public void delete(int id) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("DELETE FROM `world.entity.pets` WHERE `id` = ?");
            p.setInt(1, id);
            execute(p);
        } catch (SQLException e) {
            super.sendError("PetData delete", e);
        } finally {
            close(p);
        }
    }

    public int getNextId() {
        final DataQueue.Queue<Integer> queue = new DataQueue.Queue<>((byte) 5);
        try {
            synchronized(queue) {
                long count = DataQueue.count();
                DataQueue.queues.put(count, queue);
                Main.exchangeClient.send("DI" + queue.getType() + count);
                queue.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return queue.getValue();
    }
}
