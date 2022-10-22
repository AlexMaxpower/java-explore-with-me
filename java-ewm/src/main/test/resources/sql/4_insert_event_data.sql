INSERT INTO events (id, annotation, category_id, description, title, event_date, lat, lon, initiator_id,
                    created_on, paid, participant_limit, published_on, request_moderation, state)
VALUES (3001, 'Сплав на байдарках похож на полет.', 2001,
        'Сплав на байдарках похож на полет. На спокойной воде — это парение.' ||
        ' На бурной, порожистой — выполнение фигур высшего пилотажа. И то, и другое дарят чувство обновления,' ||
        ' феерические эмоции, яркие впечатления.',
        'Сплав на байдарках', '2028-09-29 02:38:05.000000', 55.754166, 37.62, 1001, '2022-10-21 22:19:58.214692', true,
        5, '2022-10-21 22:22:20.098033', false, 'PUBLISHED');
