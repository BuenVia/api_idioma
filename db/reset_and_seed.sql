-- =====================================================================
-- reset_and_seed.sql
--
-- Empties the local SQLite database and fills it with example data:
--   * Castilian Spanish ('ES'), B2: lessons written in Spanish
--   * Catalan ('CA'), A1: lessons written in English with Catalan examples
-- Each language: 3 categories -> 10 subjects -> 1 Markdown lesson + 5 vocab each.
-- subject.level is set per language (ES = 'B2', CA = 'A1').
-- Vocab is always term = target language, translation = English.
--
-- Run from the repo root, with the app STOPPED:
--   sqlite3 language.db < db/reset_and_seed.sql
--
-- Notes
-- * SQLite only. lesson.created_at is stored as epoch milliseconds, which
--   is how Hibernate's SQLite dialect writes it. Postgres needs a timestamp.
-- * Deletes ALL data, in every language (ES and CA).
-- * Tables must already exist (start the app once to let Hibernate create them).
-- * Rows are linked by name rather than hard-coded ids, so ids are assigned
--   by the database (they restart at 1 after the deletes).
-- * Runs in one transaction: if any statement fails, nothing is changed.
-- =====================================================================

BEGIN TRANSACTION;

-- ---------------------------------------------------------------------
-- 1. Clear everything (children first)
-- ---------------------------------------------------------------------
DELETE FROM vocab;
DELETE FROM lesson;
DELETE FROM subject;
DELETE FROM category;

-- =====================================================================
-- CASTILIAN SPANISH (ES) — B2
-- =====================================================================

-- ---------------------------------------------------------------------
-- 2. Categories
-- ---------------------------------------------------------------------
INSERT INTO category (category_name, language) VALUES
    ('Verbs: Subjunctive', 'ES'),
    ('Prepositions',       'ES'),
    ('Adverbs',            'ES');

-- ---------------------------------------------------------------------
-- 3. Subjects
-- ---------------------------------------------------------------------
INSERT INTO subject (subject_name, language, level, category_id)
SELECT v.column1, 'ES', 'B2', c.id
FROM (VALUES
    ('Subjuntivo: deseos, emociones y valoraciones',     'Verbs: Subjunctive'),
    ('Subjuntivo: duda, probabilidad y opinión negada',  'Verbs: Subjunctive'),
    ('Imperfecto de subjuntivo y oraciones condicionales', 'Verbs: Subjunctive'),
    ('Subjuntivo en oraciones temporales',               'Verbs: Subjunctive'),
    ('Por y para',                                       'Prepositions'),
    ('Verbos con preposición',                           'Prepositions'),
    ('Locuciones preposicionales',                       'Prepositions'),
    ('Adverbios en -mente',                              'Adverbs'),
    ('Adverbios de tiempo: ya, todavía, aún, apenas',    'Adverbs'),
    ('Adverbios de duda y probabilidad',                 'Adverbs')
) AS v
JOIN category c ON c.category_name = v.column2 AND c.language = 'ES';

-- ---------------------------------------------------------------------
-- 4. Lessons (one per subject; title = subject name)
-- ---------------------------------------------------------------------
INSERT INTO lesson (lesson_title, body, created_at, subject_id)
SELECT s.subject_name, v.column2, CAST(strftime('%s', 'now') AS INTEGER) * 1000, s.id
FROM (VALUES

-- Verbs: Subjunctive -------------------------------------------------
('Subjuntivo: deseos, emociones y valoraciones',
'Cuando el verbo principal expresa un **deseo**, una **emoción** o una **valoración** y los sujetos de las dos oraciones son **diferentes**, usamos **que + subjuntivo**.

| Función | Estructura | Ejemplo |
|---|---|---|
| Deseo | querer / esperar / desear + que + subjuntivo | Espero que **llegues** a tiempo. |
| Emoción | me alegra / me molesta / me da pena + que + subjuntivo | Me molesta que no me **llames**. |
| Valoración | es importante / es lógico / es una pena + que + subjuntivo | Es una pena que no **puedas** venir. |

### ¡Ojo! Mismo sujeto → infinitivo
- *Quiero **ir** al cine.* (yo – yo)
- *Quiero que **vayas** al cine.* (yo – tú)

### Ojalá
**Ojalá (que)** siempre va con subjuntivo: *Ojalá **haga** buen tiempo mañana.*'),

('Subjuntivo: duda, probabilidad y opinión negada',
'| Estructura | Modo | Ejemplo |
|---|---|---|
| Creo que / Pienso que / Me parece que | Indicativo | Creo que **tiene** razón. |
| No creo que / No pienso que / No me parece que | Subjuntivo | No creo que **tenga** razón. |
| Dudo que / Es posible que / Es probable que | Subjuntivo | Es probable que **llueva** esta tarde. |
| Es verdad que / Es evidente que / Está claro que | Indicativo | Está claro que **es** el mejor. |
| No es verdad que / No está claro que | Subjuntivo | No está claro que **sea** el mejor. |

### Regla general
- **Afirmar** una opinión o una certeza → **indicativo**.
- **Negar** una opinión o expresar **duda** → **subjuntivo**.

### En preguntas negativas
*¿No crees que **es** demasiado caro?* — aquí el hablante en realidad está afirmando, por eso se usa el indicativo.'),

('Imperfecto de subjuntivo y oraciones condicionales',
'### Formación
Se toma la 3.ª persona del plural del pretérito indefinido, se quita **-ron** y se añaden las terminaciones:

| Infinitivo | Indefinido (ellos) | Imperfecto de subjuntivo |
|---|---|---|
| hablar | habla**ron** | hablara, hablaras, hablara, habláramos, hablarais, hablaran |
| tener | tuvie**ron** | tuviera, tuvieras, tuviera, tuviéramos, tuvierais, tuvieran |
| ser / ir | fue**ron** | fuera, fueras, fuera, fuéramos, fuerais, fueran |

También existe la forma en **-se** (*hablase, tuviese*), más frecuente en la lengua escrita.

### Condicionales hipotéticas
**Si + imperfecto de subjuntivo, condicional simple**

- *Si **tuviera** más tiempo, **aprendería** a tocar la guitarra.*
- *Si **fuera** tú, no **diría** nada.*

### ¡Ojo!
Nunca se usa el condicional después de *si*: ~~Si tendría dinero...~~ → *Si **tuviera** dinero...*'),

('Subjuntivo en oraciones temporales',
'Con conectores temporales (**cuando, en cuanto, hasta que, mientras, tan pronto como**) el modo depende del momento al que nos referimos:

| Referencia | Modo | Ejemplo |
|---|---|---|
| Futuro | Subjuntivo | Cuando **llegues**, llámame. |
| Presente habitual | Indicativo | Cuando **llego** a casa, me ducho. |
| Pasado | Indicativo | Cuando **llegué**, ya se habían ido. |

### Antes de que → siempre subjuntivo
- *Salimos antes de que **empiece** a llover.*
- *Salimos antes de que **empezara** a llover.*

### ¡Ojo!
Nunca usamos el futuro después de *cuando*: ~~Cuando llegarás...~~ → *Cuando **llegues**...*'),

-- Prepositions -------------------------------------------------------
('Por y para',
'| Para | Por |
|---|---|
| **Finalidad**: Estudio para aprobar. | **Causa**: Lo hice por ti. |
| **Destino**: Salgo para Madrid. | **Movimiento a través de**: Paseamos por el parque. |
| **Plazo**: Lo necesito para el lunes. | **Tiempo aproximado**: Vuelvo por Navidad. |
| **Destinatario**: Este regalo es para ti. | **Medio**: Te lo envío por correo. |
| **Opinión**: Para mí, es la mejor opción. | **Intercambio / precio**: Lo compré por veinte euros. |
| **Comparación**: Para ser tan joven, sabe mucho. | **Agente de la pasiva**: Fue escrito por Cervantes. |

### Truco
- **Para** mira hacia el **objetivo**: adónde vamos, para qué hacemos algo.
- **Por** mira hacia la **causa** o el **camino**: por qué hacemos algo, por dónde pasamos.'),

('Verbos con preposición',
'Muchos verbos en español van seguidos de una preposición fija, que a menudo **no coincide con el inglés**.

| Preposición | Verbos | Ejemplo |
|---|---|---|
| **de** | acordarse de, depender de, darse cuenta de, quejarse de | Me di cuenta **del** error. |
| **en** | pensar en, consistir en, confiar en, insistir en | Insistió **en** pagar la cena. |
| **con** | soñar con, contar con, casarse con, enfadarse con | Sueño **con** vivir junto al mar. |
| **a** | atreverse a, negarse a, aprender a, acostumbrarse a | No me atrevo **a** decírselo. |

### Errores típicos
- ~~Me casé a María~~ → Me casé **con** María. (*married to*)
- ~~Depende en ti~~ → Depende **de** ti. (*depend on*)
- ~~Sueño de ti~~ → Sueño **contigo**. (*dream of*)

### Con oraciones
Si después viene una oración, la preposición se mantiene: *Insisto **en que** vengas.*'),

('Locuciones preposicionales',
'Las locuciones preposicionales son grupos de palabras que funcionan como una preposición. Son muy frecuentes en registros formales y en textos escritos.

| Locución | Significado | Ejemplo |
|---|---|---|
| a pesar de | despite | A pesar de la lluvia, salimos. |
| debido a | due to | El vuelo se retrasó debido a la niebla. |
| en cuanto a | as regards | En cuanto a tu pregunta, lo veremos mañana. |
| a raíz de | as a result of | A raíz del accidente, cambió de vida. |
| frente a | opposite / in contrast to | Frente a mi casa hay un parque. |
| en lugar de | instead of | En lugar de quejarte, haz algo. |

### Con verbos
Van seguidas de **infinitivo** o de **que + verbo conjugado**:
- *A pesar de **estar** cansado, fue a trabajar.*
- *A pesar de **que estaba** cansado, fue a trabajar.*

### ¡Ojo!
*En cuanto a* (= respecto a) no es lo mismo que *en cuanto* (= tan pronto como).'),

-- Adverbs ------------------------------------------------------------
('Adverbios en -mente',
'Se forman añadiendo **-mente** a la forma **femenina** del adjetivo.

| Adjetivo | Femenino | Adverbio |
|---|---|---|
| rápido | rápida | rápidamente |
| tranquilo | tranquila | tranquilamente |
| feliz | feliz | felizmente |
| fácil | fácil | fácilmente |

### Acentos
El adverbio conserva la tilde del adjetivo: *fácil → **fácilmente***, *rápida → **rápidamente***.

### Varios adverbios seguidos
Cuando hay dos o más adverbios coordinados, solo el último lleva **-mente**: *Habló **clara y tranquilamente**.*

### Alternativas frecuentes
En la lengua hablada a menudo se prefiere **con + sustantivo** o **de forma / de manera + adjetivo**:
- *con cuidado* = cuidadosamente
- *de forma rápida* = rápidamente'),

('Adverbios de tiempo: ya, todavía, aún, apenas',
'| Adverbio | Significado | Ejemplo |
|---|---|---|
| **ya** | already | Ya he terminado los deberes. |
| **ya no** | no longer | Ya no vivo en Sevilla. |
| **todavía / aún** | still / yet | Todavía no ha llegado. |
| **apenas** | hardly, barely | Apenas nos vemos últimamente. |
| **enseguida** | right away | Enseguida vuelvo. |

### Aún vs. aun
- **aún** (con tilde) = todavía: *Aún no lo sé.*
- **aun** (sin tilde) = incluso: *Aun así, lo intentaré.*

### Posición
*Ya* y *todavía* suelen ir **delante del verbo**, pero también pueden ir al final: *¿Has comido **ya**?*'),

('Adverbios de duda y probabilidad',
'| Expresión | Modo | Ejemplo |
|---|---|---|
| **a lo mejor** | Indicativo | A lo mejor **viene** mañana. |
| **igual** (coloquial) | Indicativo | Igual **llego** tarde. |
| **quizá(s) / tal vez** | Indicativo o subjuntivo | Quizás **viene** / **venga** mañana. |
| **posiblemente / probablemente** | Indicativo o subjuntivo | Probablemente **está** / **esté** en casa. |
| **seguramente** | Indicativo (normalmente) | Seguramente **tiene** razón. |

### ¿Indicativo o subjuntivo?
Con *quizás*, *tal vez* y *probablemente*, el **subjuntivo** expresa **más duda** y el **indicativo** **más seguridad**:
- *Quizás **tienes** razón.* (lo veo bastante probable)
- *Quizás **tengas** razón.* (no estoy tan seguro)

### ¡Ojo!
Si el adverbio va **después** del verbo, se usa el indicativo: *Viene **quizás** mañana.*')

) AS v
JOIN subject s ON s.subject_name = v.column1 AND s.language = 'ES';

-- ---------------------------------------------------------------------
-- 5. Vocab (5 per subject: term = Spanish, translation = English)
-- ---------------------------------------------------------------------
INSERT INTO vocab (term, translation, subject_id)
SELECT v.column2, v.column3, s.id
FROM (VALUES
-- Subjuntivo: deseos, emociones y valoraciones
('Subjuntivo: deseos, emociones y valoraciones', 'Espero que te mejores pronto.', 'I hope you get better soon.'),
('Subjuntivo: deseos, emociones y valoraciones', 'Me alegra que hayas aprobado el examen.', 'I''m glad you passed the exam.'),
('Subjuntivo: deseos, emociones y valoraciones', 'Es una pena que no puedas venir a la boda.', 'It''s a shame you can''t come to the wedding.'),
('Subjuntivo: deseos, emociones y valoraciones', 'Ojalá haga buen tiempo este fin de semana.', 'I hope the weather is nice this weekend.'),
('Subjuntivo: deseos, emociones y valoraciones', 'Me molesta que la gente hable por el móvil en el cine.', 'It bothers me when people talk on their phones at the cinema.'),

-- Subjuntivo: duda, probabilidad y opinión negada
('Subjuntivo: duda, probabilidad y opinión negada', 'No creo que sea una buena idea.', 'I don''t think it''s a good idea.'),
('Subjuntivo: duda, probabilidad y opinión negada', 'Dudo que lleguen antes de las ocho.', 'I doubt they''ll arrive before eight.'),
('Subjuntivo: duda, probabilidad y opinión negada', 'Es posible que cambien el horario del tren.', 'They may change the train timetable.'),
('Subjuntivo: duda, probabilidad y opinión negada', 'No está claro que el proyecto salga adelante.', 'It''s not clear that the project will go ahead.'),
('Subjuntivo: duda, probabilidad y opinión negada', 'No me parece que estés siendo justo con ella.', 'I don''t think you''re being fair to her.'),

-- Imperfecto de subjuntivo y oraciones condicionales
('Imperfecto de subjuntivo y oraciones condicionales', 'Si tuviera más tiempo, viajaría más.', 'If I had more time, I would travel more.'),
('Imperfecto de subjuntivo y oraciones condicionales', 'Si fuera tú, hablaría con el jefe.', 'If I were you, I would talk to the boss.'),
('Imperfecto de subjuntivo y oraciones condicionales', 'Me pidió que le ayudara con la mudanza.', 'He asked me to help him with the move.'),
('Imperfecto de subjuntivo y oraciones condicionales', 'Actúa como si no pasara nada.', 'He acts as if nothing were happening.'),
('Imperfecto de subjuntivo y oraciones condicionales', 'Si ganáramos la lotería, nos compraríamos una casa en la playa.', 'If we won the lottery, we would buy a house by the beach.'),

-- Subjuntivo en oraciones temporales
('Subjuntivo en oraciones temporales', 'Cuando termine la carrera, me iré a vivir al extranjero.', 'When I finish my degree, I''ll go and live abroad.'),
('Subjuntivo en oraciones temporales', 'En cuanto sepa algo, te aviso.', 'As soon as I know anything, I''ll let you know.'),
('Subjuntivo en oraciones temporales', 'No me iré hasta que me pidas perdón.', 'I won''t leave until you apologise to me.'),
('Subjuntivo en oraciones temporales', 'Llámame antes de que salgas de casa.', 'Call me before you leave the house.'),
('Subjuntivo en oraciones temporales', 'Mientras vivas en esta casa, seguirás mis normas.', 'As long as you live in this house, you''ll follow my rules.'),

-- Por y para
('Por y para', 'Lo hice por ti, no para ti.', 'I did it because of you, not for you.'),
('Por y para', 'Necesito el informe para el viernes.', 'I need the report by Friday.'),
('Por y para', 'Para ser extranjero, habla español muy bien.', 'For a foreigner, he speaks Spanish very well.'),
('Por y para', 'Me lo vendió por cincuenta euros.', 'He sold it to me for fifty euros.'),
('Por y para', 'El cuadro fue pintado por Goya.', 'The painting was painted by Goya.'),

-- Verbos con preposición
('Verbos con preposición', 'No me acordé de llamarte.', 'I didn''t remember to call you.'),
('Verbos con preposición', 'Todo depende de lo que decidas.', 'Everything depends on what you decide.'),
('Verbos con preposición', 'Puedes contar conmigo para lo que necesites.', 'You can count on me for whatever you need.'),
('Verbos con preposición', 'Se negó a firmar el contrato.', 'She refused to sign the contract.'),
('Verbos con preposición', 'El trabajo consiste en atender a los clientes.', 'The job consists of dealing with customers.'),

-- Locuciones preposicionales
('Locuciones preposicionales', 'A pesar del tráfico, llegamos a tiempo.', 'Despite the traffic, we arrived on time.'),
('Locuciones preposicionales', 'El partido se suspendió debido a la lluvia.', 'The match was called off due to the rain.'),
('Locuciones preposicionales', 'En cuanto al precio, todavía no hemos decidido nada.', 'As for the price, we haven''t decided anything yet.'),
('Locuciones preposicionales', 'A raíz de la crisis, muchas empresas cerraron.', 'As a result of the crisis, many companies closed.'),
('Locuciones preposicionales', 'En lugar de quejarte, intenta buscar una solución.', 'Instead of complaining, try to find a solution.'),

-- Adverbios en -mente
('Adverbios en -mente', 'Afortunadamente, nadie resultó herido.', 'Fortunately, nobody was hurt.'),
('Adverbios en -mente', 'Habló clara y tranquilamente.', 'She spoke clearly and calmly.'),
('Adverbios en -mente', 'Últimamente duermo fatal.', 'Lately I''ve been sleeping terribly.'),
('Adverbios en -mente', 'Lo resolvió fácilmente.', 'He solved it easily.'),
('Adverbios en -mente', 'Sinceramente, no me esperaba esa respuesta.', 'Honestly, I wasn''t expecting that answer.'),

-- Adverbios de tiempo: ya, todavía, aún, apenas
('Adverbios de tiempo: ya, todavía, aún, apenas', 'Ya no trabajo en esa empresa.', 'I no longer work at that company.'),
('Adverbios de tiempo: ya, todavía, aún, apenas', 'Todavía no me han contestado.', 'They still haven''t replied to me.'),
('Adverbios de tiempo: ya, todavía, aún, apenas', 'Apenas tuve tiempo de comer.', 'I barely had time to eat.'),
('Adverbios de tiempo: ya, todavía, aún, apenas', 'Aun así, decidió seguir adelante.', 'Even so, she decided to carry on.'),
('Adverbios de tiempo: ya, todavía, aún, apenas', '¿Ya has hecho la reserva?', 'Have you already made the booking?'),

-- Adverbios de duda y probabilidad
('Adverbios de duda y probabilidad', 'A lo mejor nos vemos en la fiesta.', 'Maybe we''ll see each other at the party.'),
('Adverbios de duda y probabilidad', 'Quizás tengas razón.', 'Perhaps you''re right.'),
('Adverbios de duda y probabilidad', 'Tal vez sea mejor esperar un poco.', 'Perhaps it would be better to wait a bit.'),
('Adverbios de duda y probabilidad', 'Igual llego un poco tarde.', 'I might be a bit late.'),
('Adverbios de duda y probabilidad', 'Seguramente se le ha olvidado.', 'He has probably forgotten.')
) AS v
JOIN subject s ON s.subject_name = v.column1 AND s.language = 'ES';

-- =====================================================================
-- CATALAN (CA) — A1
-- (Subjunctive is not A1 material, so the verb category is the present tense.)
-- Spelling follows the 2016 IEC reform (e.g. "soc", not "sóc").
-- =====================================================================

-- ---------------------------------------------------------------------
-- 6. Categories
-- ---------------------------------------------------------------------
INSERT INTO category (category_name, language) VALUES
    ('Verbs: Present Tense', 'CA'),
    ('Prepositions',         'CA'),
    ('Adverbs',              'CA');

-- ---------------------------------------------------------------------
-- 7. Subjects
-- ---------------------------------------------------------------------
INSERT INTO subject (subject_name, language, level, category_id)
SELECT v.column1, 'CA', 'A1', c.id
FROM (VALUES
    ('El present: verbs en -ar',            'Verbs: Present Tense'),
    ('Els verbs ser i tenir',               'Verbs: Present Tense'),
    ('Verbs reflexius: la rutina diària',   'Verbs: Present Tense'),
    ('Els verbs anar i fer',                'Verbs: Present Tense'),
    ('Preposicions de lloc',                'Prepositions'),
    ('Contraccions: al, del, pel',          'Prepositions'),
    ('Preposicions de temps',               'Prepositions'),
    ('Adverbis de freqüència',              'Adverbs'),
    ('Adverbis de lloc',                    'Adverbs'),
    ('Adverbis de temps: avui, ahir, demà', 'Adverbs')
) AS v
JOIN category c ON c.category_name = v.column2 AND c.language = 'CA';

-- ---------------------------------------------------------------------
-- 8. Lessons
-- ---------------------------------------------------------------------
INSERT INTO lesson (lesson_title, body, created_at, subject_id)
SELECT s.subject_name, v.column2, CAST(strftime('%s', 'now') AS INTEGER) * 1000, s.id
FROM (VALUES

-- Verbs: Present Tense -----------------------------------------------
('El present: verbs en -ar',
'Regular verbs ending in **-ar** follow this pattern. Example: **parlar** (to speak).

| Person | Ending | parlar |
|---|---|---|
| jo | -o | parlo |
| tu | -es | parles |
| ell / ella / vostè | -a | parla |
| nosaltres | -em | parlem |
| vosaltres | -eu | parleu |
| ells / elles / vostès | -en | parlen |

Other common -ar verbs: **treballar** (to work), **estudiar** (to study), **mirar** (to look, to watch), **escoltar** (to listen), **cantar** (to sing).

### Tip
Subject pronouns are usually left out, because the ending already shows who is doing the action: *Parlo català* = I speak Catalan.

### Negative
Put **no** before the verb: *No parlo alemany.* = I don''t speak German.'),

('Els verbs ser i tenir',
'| Person | ser (to be) | tenir (to have) |
|---|---|---|
| jo | soc | tinc |
| tu | ets | tens |
| ell / ella / vostè | és | té |
| nosaltres | som | tenim |
| vosaltres | sou | teniu |
| ells / elles / vostès | són | tenen |

### Ser
Use **ser** to say who someone is, where they are from and what they do:
- *Soc la Marta.* = I''m Marta.
- *Som de Lleida.* = We''re from Lleida.
- *Ell és metge.* = He is a doctor.

### Tenir
Use **tenir** for things you have, and for your age:
- *Tinc un germà.* = I have a brother.
- *Quants anys tens? — Tinc vint anys.* = How old are you? — I''m twenty.

### Note
Older texts write **sóc** with an accent. Since the 2016 spelling reform it is written **soc**.'),

('Verbs reflexius: la rutina diària',
'Reflexive verbs end in **-se** in the infinitive: **llevar-se** (to get up), **dutxar-se** (to have a shower), **vestir-se** (to get dressed), **dir-se** (to be called).

The reflexive pronoun goes **before** the verb:

| Person | Pronoun | llevar-se |
|---|---|---|
| jo | em | em llevo |
| tu | et | et lleves |
| ell / ella / vostè | es | es lleva |
| nosaltres | ens | ens llevem |
| vosaltres | us | us lleveu |
| ells / elles / vostès | es | es lleven |

### Before a vowel or h
**em, et, es** become **m'', t'', s''**: *M''afaito cada matí.* = I shave every morning.

### Asking someone''s name
*Com et dius? — Em dic Anna.* = What''s your name? — My name is Anna.'),

('Els verbs anar i fer',
'| Person | anar (to go) | fer (to do, to make) |
|---|---|---|
| jo | vaig | faig |
| tu | vas | fas |
| ell / ella / vostè | va | fa |
| nosaltres | anem | fem |
| vosaltres | aneu | feu |
| ells / elles / vostès | van | fan |

### Anar
**anar a** + place: *Vaig a la feina.* = I go to work. *Anem al cinema.* = We go to the cinema.

### Fer
**fer** is used for many activities and for the weather:
- *Faig esport.* = I do sport.
- *Què fas?* = What are you doing?
- *Avui fa sol / fa fred / fa calor.* = It''s sunny / cold / hot today.

### Watch out!
**vaig + infinitive** is a **past** tense, not the future: *Vaig anar a Girona.* = I went to Girona.'),

-- Prepositions -------------------------------------------------------
('Preposicions de lloc',
'| Preposition | Meaning | Example |
|---|---|---|
| a | in, at | Visc a Girona. |
| davant de | in front of | El cotxe és davant de casa. |
| darrere de | behind | El jardí és darrere de l''escola. |
| al costat de | next to | El banc és al costat del forn. |
| a sobre de | on (top of) | Les claus són a sobre de la taula. |
| a sota de | under | El gat és a sota del llit. |
| entre | between | La farmàcia és entre el banc i el bar. |

### Where is it?
To say where something is, Catalan usually uses **ser**: *On és el bany?* = Where is the bathroom?

### de + el = del
*al costat **del** forn* (not ~~de el~~). See the lesson on contractions.'),

('Contraccions: al, del, pel',
'The prepositions **a**, **de** and **per** join with the articles **el** and **els**:

| | + el | + els |
|---|---|---|
| a | al | als |
| de | del | dels |
| per | pel | pels |

- *Vaig **al** mercat.* (a + el) = I go to the market.
- *Vinc **del** gimnàs.* (de + el) = I''m coming from the gym.
- *Passegem **pel** parc.* (per + el) = We walk through the park.
- *Escric **als** meus amics.* (a + els) = I write to my friends.

### No contraction
- With feminine articles: *a la, de la, per la* → *Vaig a la platja.*
- With **l''**: *a l''escola, de l''hotel.*'),

('Preposicions de temps',
'| Expression | Use | Example |
|---|---|---|
| a les + hour | clock time | Dino a les dues. |
| a la una | one o''clock | Plego a la una. |
| el + day | one specific day | El dilluns tinc classe. |
| els + day | every week on that day | Els dimarts vaig a la piscina. |
| de ... a ... | from ... to ... | Treballo de nou a cinc. |
| al matí / a la tarda / a la nit | parts of the day | Estudio a la tarda. |

### Telling the time
- *Quina hora és?* = What time is it?
- *És la una.* = It''s one o''clock.
- *Són les tres.* = It''s three o''clock. (plural from two onwards)'),

-- Adverbs ------------------------------------------------------------
('Adverbis de freqüència',
'| Adverb | Meaning |
|---|---|
| sempre | always |
| sovint | often |
| de vegades / a vegades | sometimes |
| gairebé mai | hardly ever |
| mai | never |
| cada dia | every day |

### Mai and no
When **mai** comes after the verb, put **no** before the verb: *No bec mai cafè.* = I never drink coffee.

### Position
Frequency adverbs can go at the start of the sentence or after the verb:
- *Sempre esmorzo a casa.*
- *Vaig sovint a la biblioteca.*'),

('Adverbis de lloc',
'| Adverb | Meaning |
|---|---|
| aquí | here |
| allà / allí | there |
| a prop | near |
| lluny | far |
| a dalt | upstairs, up |
| a baix | downstairs, down |
| dins | inside |
| fora | outside |

### a prop / lluny + de
Before a place, add **de**:
- *Visc a prop **de** la platja.* = I live near the beach.
- *L''escola és lluny **de** casa.* = The school is far from home.'),

('Adverbis de temps: avui, ahir, demà',
'| Adverb | Meaning |
|---|---|
| avui | today |
| ahir | yesterday |
| demà | tomorrow |
| ara | now |
| abans | before |
| després | after, later |
| aviat | soon |
| d''hora | early |
| tard | late |

### Aviat or d''hora?
- **aviat** = soon: *Fins aviat!* = See you soon!
- **d''hora** = early: *Em llevo d''hora.* = I get up early.

### Useful expressions
*Fins demà!* (See you tomorrow!), *ara mateix* (right now), *abans de dinar* (before lunch).')

) AS v
JOIN subject s ON s.subject_name = v.column1 AND s.language = 'CA';

-- ---------------------------------------------------------------------
-- 9. Vocab
-- ---------------------------------------------------------------------
INSERT INTO vocab (term, translation, subject_id)
SELECT v.column2, v.column3, s.id
FROM (VALUES
-- El present: verbs en -ar
('El present: verbs en -ar', 'Parlo una mica de català.', 'I speak a little Catalan.'),
('El present: verbs en -ar', 'On treballes?', 'Where do you work?'),
('El present: verbs en -ar', 'Estudiem anglès a l''escola.', 'We study English at school.'),
('El present: verbs en -ar', 'Ella escolta música cada dia.', 'She listens to music every day.'),
('El present: verbs en -ar', 'No mirem la televisió.', 'We don''t watch television.'),

-- Els verbs ser i tenir
('Els verbs ser i tenir', 'Soc de Barcelona.', 'I''m from Barcelona.'),
('Els verbs ser i tenir', 'Ets estudiant?', 'Are you a student?'),
('Els verbs ser i tenir', 'Tinc dos germans.', 'I have two brothers.'),
('Els verbs ser i tenir', 'Quants anys tens?', 'How old are you?'),
('Els verbs ser i tenir', 'La meva mare és professora.', 'My mother is a teacher.'),

-- Verbs reflexius: la rutina diària
('Verbs reflexius: la rutina diària', 'Em llevo a les set.', 'I get up at seven.'),
('Verbs reflexius: la rutina diària', 'Et dutxes al matí o a la nit?', 'Do you have a shower in the morning or at night?'),
('Verbs reflexius: la rutina diària', 'Com es diu el teu germà?', 'What''s your brother''s name?'),
('Verbs reflexius: la rutina diària', 'Ens vestim i esmorzem.', 'We get dressed and have breakfast.'),
('Verbs reflexius: la rutina diària', 'Els nens es lleven tard el diumenge.', 'The children get up late on Sundays.'),

-- Els verbs anar i fer
('Els verbs anar i fer', 'Vaig a la feina amb bicicleta.', 'I go to work by bike.'),
('Els verbs anar i fer', 'Què fas aquest cap de setmana?', 'What are you doing this weekend?'),
('Els verbs anar i fer', 'Avui fa molt de fred.', 'It''s very cold today.'),
('Els verbs anar i fer', 'Anem a la platja?', 'Shall we go to the beach?'),
('Els verbs anar i fer', 'Els dissabtes fem una paella.', 'On Saturdays we make a paella.'),

-- Preposicions de lloc
('Preposicions de lloc', 'Visc a Tarragona.', 'I live in Tarragona.'),
('Preposicions de lloc', 'On és el bany?', 'Where is the bathroom?'),
('Preposicions de lloc', 'La farmàcia és al costat del supermercat.', 'The pharmacy is next to the supermarket.'),
('Preposicions de lloc', 'Les claus són a sobre de la taula.', 'The keys are on the table.'),
('Preposicions de lloc', 'El gat dorm a sota del llit.', 'The cat sleeps under the bed.'),

-- Contraccions: al, del, pel
('Contraccions: al, del, pel', 'Anem al cinema aquesta nit.', 'We''re going to the cinema tonight.'),
('Contraccions: al, del, pel', 'Vinc del mercat.', 'I''m coming from the market.'),
('Contraccions: al, del, pel', 'Passegem pel parc.', 'We walk through the park.'),
('Contraccions: al, del, pel', 'Truco als meus pares cada diumenge.', 'I call my parents every Sunday.'),
('Contraccions: al, del, pel', 'És la casa dels meus avis.', 'It''s my grandparents'' house.'),

-- Preposicions de temps
('Preposicions de temps', 'Quina hora és?', 'What time is it?'),
('Preposicions de temps', 'Dino a les dues.', 'I have lunch at two.'),
('Preposicions de temps', 'Els dimarts vaig a la piscina.', 'On Tuesdays I go to the swimming pool.'),
('Preposicions de temps', 'Treballo de nou a cinc.', 'I work from nine to five.'),
('Preposicions de temps', 'Estudio català a la tarda.', 'I study Catalan in the afternoon.'),

-- Adverbis de freqüència
('Adverbis de freqüència', 'Sempre esmorzo a casa.', 'I always have breakfast at home.'),
('Adverbis de freqüència', 'Vaig sovint a la biblioteca.', 'I often go to the library.'),
('Adverbis de freqüència', 'De vegades cuino per als amics.', 'Sometimes I cook for my friends.'),
('Adverbis de freqüència', 'No bec mai cafè.', 'I never drink coffee.'),
('Adverbis de freqüència', 'Gairebé mai miro la televisió.', 'I hardly ever watch television.'),

-- Adverbis de lloc
('Adverbis de lloc', 'Vine aquí, si us plau.', 'Come here, please.'),
('Adverbis de lloc', 'El restaurant és allà.', 'The restaurant is over there.'),
('Adverbis de lloc', 'Visc a prop de l''estació.', 'I live near the station.'),
('Adverbis de lloc', 'La platja és molt lluny?', 'Is the beach very far?'),
('Adverbis de lloc', 'Els nens juguen fora.', 'The children are playing outside.'),

-- Adverbis de temps: avui, ahir, demà
('Adverbis de temps: avui, ahir, demà', 'Avui és dilluns.', 'Today is Monday.'),
('Adverbis de temps: avui, ahir, demà', 'Demà tinc un examen.', 'I have an exam tomorrow.'),
('Adverbis de temps: avui, ahir, demà', 'Ara no puc parlar.', 'I can''t talk now.'),
('Adverbis de temps: avui, ahir, demà', 'Em llevo d''hora per anar a treballar.', 'I get up early to go to work.'),
('Adverbis de temps: avui, ahir, demà', 'Fins aviat!', 'See you soon!')
) AS v
JOIN subject s ON s.subject_name = v.column1 AND s.language = 'CA';

COMMIT;
