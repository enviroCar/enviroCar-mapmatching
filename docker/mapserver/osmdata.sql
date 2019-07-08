--
-- PostgreSQL database dump
--

-- Dumped from database version 10.4 (Debian 10.4-2.pgdg90+1)
-- Dumped by pg_dump version 10.4 (Debian 10.4-2.pgdg90+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


--
-- Name: hstore; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS hstore WITH SCHEMA public;


--
-- Name: EXTENSION hstore; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION hstore IS 'data type for storing sets of (key, value) pairs';


--
-- Name: postgis; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS postgis WITH SCHEMA public;


--
-- Name: EXTENSION postgis; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION postgis IS 'PostGIS geometry, geography, and raster spatial types and functions';


--
-- Name: osmosisupdate(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.osmosisupdate() RETURNS void
    LANGUAGE plpgsql
    AS $$
DECLARE
BEGIN
END;
$$;


ALTER FUNCTION public.osmosisupdate() OWNER TO postgres;

--
-- Name: unnest_bbox_way_nodes(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.unnest_bbox_way_nodes() RETURNS void
    LANGUAGE plpgsql
    AS $$
DECLARE
	previousId ways.id%TYPE;
	currentId ways.id%TYPE;
	result bigint[];
	wayNodeRow way_nodes%ROWTYPE;
	wayNodes ways.nodes%TYPE;
BEGIN
	FOR wayNodes IN SELECT bw.nodes FROM bbox_ways bw LOOP
		FOR i IN 1 .. array_upper(wayNodes, 1) LOOP
			INSERT INTO bbox_way_nodes (id) VALUES (wayNodes[i]);
		END LOOP;
	END LOOP;
END;
$$;


ALTER FUNCTION public.unnest_bbox_way_nodes() OWNER TO postgres;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: bfmap_ways; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.bfmap_ways (
    gid bigint NOT NULL,
    osm_id bigint NOT NULL,
    class_id integer NOT NULL,
    source bigint NOT NULL,
    target bigint NOT NULL,
    length double precision NOT NULL,
    reverse double precision NOT NULL,
    maxspeed_forward integer,
    maxspeed_backward integer,
    priority double precision NOT NULL,
    geom public.geometry(LineString,4326)
);


ALTER TABLE public.bfmap_ways OWNER TO postgres;

--
-- Name: bfmap_ways_gid_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.bfmap_ways_gid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.bfmap_ways_gid_seq OWNER TO postgres;

--
-- Name: bfmap_ways_gid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.bfmap_ways_gid_seq OWNED BY public.bfmap_ways.gid;


--
-- Name: nodes; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.nodes (
    id bigint NOT NULL,
    version integer NOT NULL,
    user_id integer NOT NULL,
    tstamp timestamp without time zone NOT NULL,
    changeset_id bigint NOT NULL,
    tags public.hstore,
    geom public.geometry(Point,4326)
);


ALTER TABLE public.nodes OWNER TO postgres;

--
-- Name: relation_members; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.relation_members (
    relation_id bigint NOT NULL,
    member_id bigint NOT NULL,
    member_type character(1) NOT NULL,
    member_role text NOT NULL,
    sequence_id integer NOT NULL
);
ALTER TABLE ONLY public.relation_members ALTER COLUMN relation_id SET (n_distinct=-0.09);
ALTER TABLE ONLY public.relation_members ALTER COLUMN member_id SET (n_distinct=-0.62);
ALTER TABLE ONLY public.relation_members ALTER COLUMN member_role SET (n_distinct=6500);
ALTER TABLE ONLY public.relation_members ALTER COLUMN sequence_id SET (n_distinct=10000);


ALTER TABLE public.relation_members OWNER TO postgres;

--
-- Name: relations; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.relations (
    id bigint NOT NULL,
    version integer NOT NULL,
    user_id integer NOT NULL,
    tstamp timestamp without time zone NOT NULL,
    changeset_id bigint NOT NULL,
    tags public.hstore
);


ALTER TABLE public.relations OWNER TO postgres;

--
-- Name: schema_info; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.schema_info (
    version integer NOT NULL
);


ALTER TABLE public.schema_info OWNER TO postgres;

--
-- Name: temp_ways; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.temp_ways (
    way_id bigint,
    tags public.hstore,
    seq integer[],
    nodes bigint[],
    counts bigint[],
    geoms bytea[]
);


ALTER TABLE public.temp_ways OWNER TO postgres;

--
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    id integer NOT NULL,
    name text NOT NULL
);


ALTER TABLE public.users OWNER TO postgres;

--
-- Name: way_nodes; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.way_nodes (
    way_id bigint NOT NULL,
    node_id bigint NOT NULL,
    sequence_id integer NOT NULL
);
ALTER TABLE ONLY public.way_nodes ALTER COLUMN way_id SET (n_distinct=-0.08);
ALTER TABLE ONLY public.way_nodes ALTER COLUMN node_id SET (n_distinct=-0.83);
ALTER TABLE ONLY public.way_nodes ALTER COLUMN sequence_id SET (n_distinct=2000);


ALTER TABLE public.way_nodes OWNER TO postgres;

--
-- Name: ways; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ways (
    id bigint NOT NULL,
    version integer NOT NULL,
    user_id integer NOT NULL,
    tstamp timestamp without time zone NOT NULL,
    changeset_id bigint NOT NULL,
    tags public.hstore,
    nodes bigint[]
);


ALTER TABLE public.ways OWNER TO postgres;

--
-- Name: bfmap_ways gid; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bfmap_ways ALTER COLUMN gid SET DEFAULT nextval('public.bfmap_ways_gid_seq'::regclass);


--
-- Name: nodes pk_nodes; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nodes
    ADD CONSTRAINT pk_nodes PRIMARY KEY (id);


--
-- Name: relation_members pk_relation_members; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.relation_members
    ADD CONSTRAINT pk_relation_members PRIMARY KEY (relation_id, sequence_id);


--
-- Name: relations pk_relations; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.relations
    ADD CONSTRAINT pk_relations PRIMARY KEY (id);


--
-- Name: schema_info pk_schema_info; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.schema_info
    ADD CONSTRAINT pk_schema_info PRIMARY KEY (version);


--
-- Name: users pk_users; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT pk_users PRIMARY KEY (id);


--
-- Name: way_nodes pk_way_nodes; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.way_nodes
    ADD CONSTRAINT pk_way_nodes PRIMARY KEY (way_id, sequence_id);


--
-- Name: ways pk_ways; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ways
    ADD CONSTRAINT pk_ways PRIMARY KEY (id);


--
-- Name: idx_bfmap_ways_geom; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_bfmap_ways_geom ON public.bfmap_ways USING gist (geom);


--
-- Name: idx_nodes_geom; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_nodes_geom ON public.nodes USING gist (geom);


--
-- Name: idx_relation_members_member_id_and_type; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_relation_members_member_id_and_type ON public.relation_members USING btree (member_id, member_type);


--
-- Name: idx_way_nodes_node_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_way_nodes_node_id ON public.way_nodes USING btree (node_id);


--
-- PostgreSQL database dump complete
--

