package com.urosdragojevic.realbookstore.repository;

import com.urosdragojevic.realbookstore.audit.AuditLogger;
import com.urosdragojevic.realbookstore.domain.Genre;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class GenreRepository {

    private static final Logger LOG = LoggerFactory.getLogger(GenreRepository.class);


    private final DataSource dataSource;

    public GenreRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Genre> getAll() {
        List<Genre> genreList = new ArrayList<>();
        String query = "SELECT id, name FROM genres";
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query)) {
            while (rs.next()) {
                genreList.add(new Genre(rs.getInt(1), rs.getString(2)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            LOG.warn("Failed to get genres");
        }
        AuditLogger.getAuditLogger(BookRepository.class).audit("Successfully retreived genres");
        return genreList;
    }

    public List<Genre> getAllForBook(int bookId) {
        List<Genre> genreList = new ArrayList<>();
        String query = "SELECT genres.id, genres.name FROM genres, books_to_genres WHERE books_to_genres.bookId = " + bookId + "AND genres.id = books_to_genres.genreId";
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query)) {
            while (rs.next()) {
                genreList.add(new Genre(rs.getInt(1), rs.getString(2)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            LOG.warn("Failed to get all genres for book " + bookId);
        }
        AuditLogger.getAuditLogger(BookRepository.class).audit("Successfully retreived genres for book " + bookId);
        return genreList;
    }

}
