-- Разрешаем одинаковые названия категорий у пользователя, если у них разный тип
-- (нужно для сидов по умолчанию: "Прочее" есть и в EXPENSE, и в INCOME)
ALTER TABLE categories DROP CONSTRAINT uq_categories_user_name;
ALTER TABLE categories ADD CONSTRAINT uq_categories_user_name_type UNIQUE (user_id, name, type);
