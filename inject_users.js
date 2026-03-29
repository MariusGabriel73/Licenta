const { Client } = require('pg');
const client = new Client({
  user: 'postgres',
  password: '2073',
  host: 'localhost',
  port: 5432,
  database: 'medicalsystem',
});

const medicHash = '$2a$10$JTlJ7olpGcUlnjebok5Il.xNKZuenoRJHQOZhWkryJxBB.mj5F8S2';
const pacientHash = '$2a$10$wZRrM0hADkcwOyjjsjZVf.mPZGGcXFSth1Tcd7MKllksoEVPJPsoC';

async function run() {
  try {
    await client.connect();

    let existingMedics = await client.query("SELECT COUNT(*) FROM jhi_user WHERE login='medic1'");
    if (existingMedics.rows[0].count > 0) {
      console.log('Medic1 already exists, aborting logic.');
      return;
    }

    await client.query('BEGIN');

    // Medics
    for (let i = 1; i <= 10; i++) {
      let userId = 4 + i;
      await client.query(
        `
                INSERT INTO jhi_user (id, login, password_hash, first_name, last_name, email, activated, lang_key, created_by, last_modified_by)
                VALUES ($1, $2, $3, $4, $5, $6, true, 'en', 'system', 'system')
            `,
        [userId, `medic${i}`, medicHash, 'Medic', `${i}`, `medic${i}@localhost`],
      );

      await client.query(`INSERT INTO jhi_user_authority (user_id, authority_name) VALUES ($1, 'ROLE_USER')`, [userId]);
      await client.query(`INSERT INTO jhi_user_authority (user_id, authority_name) VALUES ($1, 'ROLE_MEDIC')`, [userId]);
      await client.query(`UPDATE medic SET user_id = $1 WHERE id = $2`, [userId, i]);
    }

    // Pacients
    for (let i = 1; i <= 10; i++) {
      let userId = 14 + i;
      await client.query(
        `
                INSERT INTO jhi_user (id, login, password_hash, first_name, last_name, email, activated, lang_key, created_by, last_modified_by)
                VALUES ($1, $2, $3, $4, $5, $6, true, 'en', 'system', 'system')
            `,
        [userId, `pacient${i}`, pacientHash, 'Pacient', `${i}`, `pacient${i}@localhost`],
      );

      await client.query(`INSERT INTO jhi_user_authority (user_id, authority_name) VALUES ($1, 'ROLE_USER')`, [userId]);
      await client.query(`INSERT INTO jhi_user_authority (user_id, authority_name) VALUES ($1, 'ROLE_PACIENT')`, [userId]);
      await client.query(`UPDATE pacient SET user_id = $1 WHERE id = $2`, [userId, i]);
    }

    await client.query('COMMIT');
    console.log('Successfully injected all 20 users!');
  } catch (e) {
    await client.query('ROLLBACK');
    console.error('Error inserting:', e);
  } finally {
    await client.end();
  }
}
run();
